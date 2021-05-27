package algorithms;

import arwdatastruct.Agent;
import orderpicking.Pick;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class DynamicProgrammingOneAgentPerExit extends Algorithm<MultiExitProblem>{

    public Solution solve(MultiExitProblem problem){

//        Solution solution = new Solution();
//        SingleOrderDyn singleOrderDyn = new SingleOrderDyn();
//        for(Problem oneExitProblem: problemsPerExit.values()){
//            Solution oneExitSolution = singleOrderDyn.solve(oneExitProblem);
//            Agent agent = oneExitProblem.getAgents().get(0);
//            solution.addRoute(agent, oneExitSolution.getRoute(agent));
//        }

        Solution solution = new Solution();
        SingleOrderDyn singleOrderDyn = new SingleOrderDyn();
        for(OneExitProblem oneExitProblem: problem.getProblems()){
            Solution oneExitSolution = singleOrderDyn.solve(oneExitProblem);
            Agent agent = oneExitProblem.getAgent();
            solution.addRoute(agent, oneExitSolution.getRoute(agent));
        }


        return solution;
    }
}
