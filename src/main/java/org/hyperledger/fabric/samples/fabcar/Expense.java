/**
 * ymk
 */
package org.hyperledger.fabric.samples.fabcar;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;



@DataType
public final class Expense {

    @Property()
    private final String projectKey;

    @Property()
    private final String amount;

    @Property()
    private final String comment;

    @Property()
    private final String timestamp;

    @Property()
    private final String type;

    public Expense(@JsonProperty("projectKey") String projectKey,@JsonProperty("amount") String amount,
                   @JsonProperty("comment")String comment, @JsonProperty("timestamp") String timestamp) {
        this.projectKey = projectKey;
        this.amount = amount;
        this.comment = comment;
        this.timestamp = timestamp;
        this.type = "expense";
    }

    public String getProjectKey() {
        return projectKey;
    }

    public String getAmount() {
        return amount;
    }

    public String getComment(){
        return comment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getType(){
        return type;
    }
}
