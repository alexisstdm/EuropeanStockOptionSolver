package derivatives.financial.europeanstockoptionsolver;

/**
 * Created by casa on 25/11/14.
 */

import java.util.HashMap;
import mathematical.solver.Function;
import european.options.BlackScholesModel;
import european.options.EuropeanPut;

public class EuropeanPutSolverFunction implements Function {
    @Override
    public Double evaluate(HashMap<String,Double> parameters){
        BlackScholesModel myModel = new BlackScholesModel(parameters.get("SPOT_LEVEL"),
                                                          parameters.get("VOLATILITY"),
                                                          parameters.get("RATE"),
                                                          parameters.get("DIVIDEND"));
        EuropeanPut myPut = new EuropeanPut(myModel, parameters.get("STRIKE"),
                                                     parameters.get("MATURITY"));
        return myPut.getPrice(true)-parameters.get("PRICE");
    }
}
