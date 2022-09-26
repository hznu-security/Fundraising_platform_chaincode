/**
 * ymk
 */
package org.hyperledger.fabric.samples.fabcar;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;


@DataType
public final class Contribution {
    @Property()
    private final String userId;

    @Property()
    private final String projectKey;

    @Property()
    private final String amount;

    @Property()
    private final String timestamp;

    @Property()
    private final String type;

    public Contribution(@JsonProperty("userId")String userId, @JsonProperty("projectKey") String projectKey,
                        @JsonProperty("amount") String amount,@JsonProperty("timestamp") String timestamp) {
        this.userId = userId;
        this.projectKey = projectKey;
        this.amount = amount;
        this.timestamp = timestamp;
        this.type = "contribution";
    }

    public String getUserId() {
        return userId;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public String getAmount() {
        return amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getType(){
        return type;
    }
}
