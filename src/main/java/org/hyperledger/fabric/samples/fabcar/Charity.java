package org.hyperledger.fabric.samples.fabcar;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;


@DataType
public class Charity {
    @Property()
    private String Balance;

    public Charity(@JsonProperty("balance") String balance){
        this.Balance = balance;
    }

    public String getBalance(){
        return Balance;
    }

    public void setBalance(double newBalance){
        this.Balance = ""+newBalance;
    }
}
