/**
 * ymk
 */
package org.hyperledger.fabric.samples.fabcar;

import java.util.ArrayList;
import java.util.List;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import com.owlike.genson.Genson;


/**
 * Java implementation of the Fabric Car Contract described in the Writing Your
 * First Application tutorial
 */
@Contract(
        name = "Platform",
        info = @Info(
                title = "Platform contract",
                description = "The hyperledger project contract",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "f.carr@example.com",
                        name = "F Carr",
                        url = "https://hyperledger.example.com")))
@Default
public final class Platform implements ContractInterface {

    private final Genson genson = new Genson();

    @Transaction()
    public Project queryProject(final Context ctx, final String key) {
        ChaincodeStub stub = ctx.getStub();
        String projectState = stub.getStringState(key);

        if (projectState.isEmpty()) {
            String errorMessage = String.format("Project %s does not exist", key);
            throw new ChaincodeException(errorMessage);
        }

        Project project = genson.deserialize(projectState, Project.class);

        return project;
    }

    @Transaction()
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

    }

    @Transaction()
    public Project createProject(final Context ctx, final String key, final String projectName, final String charityId,
                                 final String comment, final String target, final String timestamp,final String materialPath,
                                 final String endTime,final String status) {
        ChaincodeStub stub = ctx.getStub();
        String projectState = stub.getStringState(key);
        if (!projectState.isEmpty()) {
            String errorMessage = String.format("Project %s already exists", key);
            throw new ChaincodeException(errorMessage);
        }
        Project project = new Project(key,projectName,charityId,comment,target,"0", timestamp,materialPath,endTime,status,"0");
        projectState = genson.serialize(project);
        stub.putStringState(key, projectState);


        return project;
    }

    @Transaction()
    public Project[] queryAllProjects(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        final String startKey = "PROJECT0";
        final String endKey = "PROJECT999";
        List<Project> projects = new ArrayList<Project>();

        QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey, endKey);

        for (KeyValue result: results) {
            Project project = genson.deserialize(result.getStringValue(), Project.class);
            projects.add(project);
        }

        Project[] response = projects.toArray(new Project[projects.size()]);

        return response;
    }

//    @Transaction()
//    public Project updateProjectCurrent(final Context ctx, final String key, final String changement) {
//        ChaincodeStub stub = ctx.getStub();
//
//        String projectState = stub.getStringState(key);
//
//        if(projectState.isEmpty()){
//           String errorMessage = String.format("Project %s does not exists",key);
//           throw new ChaincodeException(errorMessage);
//        }
//
//        Project project = genson.deserialize(projectState,Project.class);
//        double newCurrent=Double.parseDouble(project.getCurrent());
//        double change = Double.parseDouble(changement);
//        double newTotalExpense = Double.parseDouble(project.getTotalExpense());
//        if(change>0){
//            newCurrent = change+newCurrent;
//        }else{
//            newTotalExpense = -change+Double.parseDouble(project.getTotalExpense());
//        }
//        Project newProject = new Project(project.getProjectKey(),project.getProjectName(),project.getCharityId(),
//                project.getComment(),project.getTarget(),
//                newCurrent+"",project.getTimestamp(),project.getMaterialPath(),project.getEndTime(),project.getStatus(),newTotalExpense+"");
//
//        String newProjectState = genson.serialize(newProject);
//
//        stub.putStringState(key,newProjectState);
//
//        return newProject;
//    }

    @Transaction()
    public Project updateProjectStatus(final Context ctx, final String key, final String newState) {
        ChaincodeStub stub = ctx.getStub();

        String projectState = stub.getStringState(key);

        if(projectState.isEmpty()){
            String errorMessage = String.format("Project %s does not exists",key);
            throw new ChaincodeException(errorMessage);
        }

        Project project = genson.deserialize(projectState,Project.class);

        Project newProject = new Project(project.getProjectKey(),project.getProjectName(),project.getCharityId(),
                project.getComment(),project.getTarget(),
                project.getCurrent()+"",project.getTimestamp(),project.getMaterialPath(),
                project.getEndTime(),newState,project.getTotalExpense());

        String newProjectState = genson.serialize(newProject);

        stub.putStringState(key,newProjectState);

        return newProject;
    }

    @Transaction()
    public Project[] queryProjectsByCharityId(final Context ctx, final String charityId){
        ChaincodeStub stub = ctx.getStub();
        String queryString = String.format("{\"selector\":{\"charityId\":\"%s\",\"type\":\"project\"}}",charityId);
        QueryResultsIterator<KeyValue> results = stub.getQueryResult(queryString);
        ArrayList<Project> projects = new ArrayList<>();

        for(KeyValue result:results){
            String projectState = result.getStringValue();
            Project project = genson.deserialize(projectState,Project.class);
            projects.add(project);
        }
        Project [] response = projects.toArray(new Project[projects.size()]);
        return response;
    }

    @Transaction()
    public Contribution createContribution(final Context ctx, final String key, final String userId,
                                           final String projectKey, final String charityKey,final String amount,
                                           final String timestamp)  {
            ChaincodeStub stub = ctx.getStub();

            String contributionState = stub.getStringState(key);
            if(!contributionState.isEmpty()){
                String errorMessage = String.format("Contribution %s already exists");
                throw new ChaincodeException(errorMessage);
            }

            Contribution contribution = new Contribution(userId,projectKey,amount,timestamp);
            contributionState = genson.serialize(contribution);

            //更新项目余额
            String projectState = stub.getStringState(projectKey);
            Project project = genson.deserialize(projectState,Project.class);
            double current = Double.parseDouble(project.getCurrent());
            double damount = Double.parseDouble(amount);
            project.setCurrent(current+damount);

            //更新机构余额
            String charityState = stub.getStringState(charityKey);
            Charity charity;
            if(charityState.isEmpty()){
                charity = new Charity(amount+"");
            }else{
                charity = genson.deserialize(charityState,Charity.class);
                double balance = Double.parseDouble(charity.getBalance());
                charity.setBalance(balance+damount);
            }

            stub.putStringState(projectKey,genson.serialize(project));
            stub.putStringState(charityKey,genson.serialize(charity));
            stub.putStringState(key,contributionState);

            return contribution;
    }

    @Transaction()
    public Contribution queryContribution(final Context ctx, final String key) {

        ChaincodeStub stub = ctx.getStub();

        String contributionState = stub.getStringState(key);
        if(contributionState.isEmpty()){
           String errorMessage = String.format("Contribution %s does not exist",key);
            throw new ChaincodeException(errorMessage);
        }

        Contribution contribution = genson.deserialize(contributionState,Contribution.class);

        return contribution;
    }

    @Transaction()
    public Contribution[] queryAllContributions(final Context ctx){

        ChaincodeStub stub = ctx.getStub();

        final String startKey = "CONTRIBUTION0";
        final String endKey = "CONTRIBUTION999";
        ArrayList<Contribution> contributions = new ArrayList<>();
        QueryResultsIterator<KeyValue> resultsIterator = stub.getStateByRange(startKey,endKey);

        for(KeyValue result : resultsIterator) {
            Contribution contribution = genson.deserialize(result.getStringValue(),Contribution.class);
            contributions.add(contribution);
        }

        Contribution[] response = contributions.toArray(new Contribution[contributions.size()]);

        return response;
    }

    @Transaction()
    public Contribution[] queryContributionsByUserId(final Context ctx, final String userID){
        ChaincodeStub stub = ctx.getStub();

        String queryString =  String.format("{\"selector\":{\"userId\":\"%s\",\"type\":\"contribution\"}}",userID);

        QueryResultsIterator<KeyValue> results = stub.getQueryResult(queryString);
        ArrayList<Contribution> contributions = new ArrayList<>();

        for(KeyValue result:results){
            String  contributionState = result.getStringValue();
            Contribution contribution = genson.deserialize(contributionState,Contribution.class);
            contributions.add(contribution);
        }
        Contribution [] response = contributions.toArray(new Contribution[contributions.size()]);
        return response;
    }

    @Transaction()
    public Contribution[] queryContributionsByProjectKey(final Context ctx,final String projectKey){
        ChaincodeStub stub = ctx.getStub();

        String queryString = String.format("{\"selector\":{\"projectKey\":\"%s\",\"type\":\"contribution\"}}",projectKey);
        QueryResultsIterator<KeyValue> results = stub.getQueryResult(queryString);
        ArrayList<Contribution> contributions = new ArrayList<>();

        for(KeyValue result:results){
            String contributionState = result.getStringValue();
            Contribution contribution = genson.deserialize(contributionState,Contribution.class);
            contributions.add(contribution);
        }
        Contribution[] response = contributions.toArray(new Contribution[contributions.size()]);

        return response;
    }

    //amount是一个负数
    @Transaction()
    public Expense createExpense(final Context ctx, final String key, final String projectKey, final String charityKey,
                                 final String amount, final String comment, final String timestamp){

        ChaincodeStub stub = ctx.getStub();

        String expenseState = stub.getStringState(key);
        if(!expenseState.isEmpty()){
            String errorMessage = String.format("EXPENSE %s already exists");
            throw new ChaincodeException(errorMessage);
        }

        Expense expense = new Expense(projectKey,amount,comment,timestamp);
        expenseState = genson.serialize(expense);

        String projectState = stub.getStringState(projectKey);
        String charityState = stub.getStringState(charityKey);

        Project project = genson.deserialize(projectState,Project.class);
        Charity charity = genson.deserialize(charityState,Charity.class);

        double current = Double.parseDouble(project.getCurrent());
        double totalExpense = Double.parseDouble(project.getTotalExpense());
        double damount = Double.parseDouble(amount);
        double balance = Double.parseDouble(charity.getBalance());

        project.setCurrent(current+damount);
        project.setTotalExpense(totalExpense-damount);
        charity.setBalance(balance+damount);

        stub.putStringState(projectKey,genson.serialize(project));
        stub.putStringState(charityKey,genson.serialize(charity));
        stub.putStringState(key,expenseState);

        return expense;
    }

    @Transaction()
    public Expense[] queryAllExpenses(final Context ctx){
        ChaincodeStub stub = ctx.getStub();

        final String startKey = "EXPENSE0";
        final String endKey = "EXPENSE999";
        ArrayList<Expense> expenses = new ArrayList<>();

        QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey,endKey);

        for(KeyValue result : results){
            String expenseState = result.getStringValue();
            Expense expense = genson.deserialize(expenseState,Expense.class);
            expenses.add(expense);
        }

        Expense [] response = expenses.toArray(new Expense[expenses.size()]);

        return response;
    }
    @Transaction
    public Expense queryExpense(final Context ctx,final String key){

        ChaincodeStub stub = ctx.getStub();

        String expenseState = stub.getStringState(key);
        if(expenseState.isEmpty()){
            String errorMessage = String.format("Expense %s does not exists",key);
            throw new ChaincodeException(errorMessage);
        }

        Expense expense = genson.deserialize(expenseState,Expense.class);

        return expense;
    }

    @Transaction
    public Expense[] queryExpensesByProjectKey(final Context ctx,final String projectKey){
        ChaincodeStub stub = ctx.getStub();
        String queryString = String.format("{\"selector\":{\"projectKey\":\"%s\",\"type\":\"expense\"}}",projectKey);
        QueryResultsIterator<KeyValue> results = stub.getQueryResult(queryString);
        ArrayList<Expense> expenses = new ArrayList<>();
        for(KeyValue result : results){
            String expenseState = result.getStringValue();
            Expense expense = genson.deserialize(expenseState,Expense.class);
            expenses.add(expense);
        }
        Expense [] response = expenses.toArray(new Expense[expenses.size()]);
        return response;
    }

}