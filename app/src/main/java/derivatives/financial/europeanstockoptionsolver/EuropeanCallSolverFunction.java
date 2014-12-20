package derivatives.financial.europeanstockoptionsolver;

import mathematical.solver.Function;
import european.options.BlackScholesModel;
import european.options.EuropeanCall;
import java.util.HashMap;

/**
 * Created by casa on 25/11/14.
 */
public class EuropeanCallSolverFunction implements Function {
    @Override
    public Double evaluate(HashMap<String,Double> parameters){
       BlackScholesModel myModel = new BlackScholesModel(parameters.get("SPOT_LEVEL"),
                                                       parameters.get("VOLATILITY"),
                                                       parameters.get("RATE"),
                                                       parameters.get("DIVIDEND"));
       EuropeanCall myCall = new EuropeanCall(myModel, parameters.get("STRIKE"),
                                                       parameters.get("MATURITY"));
       return myCall.getPrice(true)-parameters.get("PRICE");
    }
}
