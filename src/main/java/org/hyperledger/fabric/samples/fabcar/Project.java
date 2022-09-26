/**
 * ymk
 */
package org.hyperledger.fabric.samples.fabcar;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class Project {

    @Property
    private final String projectKey;

    @Property()
    private final String projectName;

    @Property()
    private final String charityId;

    @Property()
    private final String comment;

    @Property()
    private final String target;

    @Property()
    private String current;

    @Property()
    private String totalExpense;

    @Property
    private final String timestamp;

    @Property
    private final String materialPath;

    @Property
    private final String endTime;

    @Property
    private final String status;

    @Property
    private final String type;

    public String getProjectKey(){
        return projectKey;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getCharityId() {
        return charityId;
    }

    public String getComment() {
        return comment;
    }

    public String getTarget() {
        return target;
    }

    public String getCurrent() {
        return current;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getMaterialPath(){
        return materialPath;
    }

    public String getEndTime(){
        return endTime;
    }

    public String getStatus(){
        return status;
    }

    public String getType(){
        return type;
    }

    public String getTotalExpense(){
        return totalExpense;
    }

    public void setCurrent(double newCurrent){
        this.current = newCurrent+"";
    }

    public void setTotalExpense(double newExpense){
        this.totalExpense = newExpense+"";
    }

    public Project(@JsonProperty("projectKey")String projectKey,@JsonProperty("projectName") String projectName,
                   @JsonProperty("charityId") String charityId, @JsonProperty("comment") String comment,
                   @JsonProperty("target") String target, @JsonProperty("current") String current,
                   @JsonProperty("timestamp") String timestamp,@JsonProperty("materialPath") String materialPath,
                   @JsonProperty("endTime")String endTime,
                   @JsonProperty("status")String status,@JsonProperty("totalExpense") String totalExpense) {
        this.projectKey = projectKey;
        this.projectName = projectName;
        this.charityId = charityId;
        this.comment = comment;
        this.target = target;
        this.current = current;
        this.timestamp = timestamp;
        this.materialPath = materialPath;
        this.endTime = endTime;
        this.status = status;
        this.totalExpense=totalExpense;
        this.type="project";
    }


}
