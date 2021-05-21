package algorithms;

import arwdatastruct.Agent;
import orderpicking.Pick;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class DynamicProgrammingOneAgentPerExit extends Algorithm{

    public Solution solve(Problem problem){

        //Separar os picks por saída
        HashMap<String, Problem> problemsPerExit = new HashMap<>();
        for (Pick pick : problem.getPicks()){
            if(!problemsPerExit.containsKey(pick.getDestiny())){
                problemsPerExit.put(pick.getDestiny(), new Problem(problem.getGraph()));
            }
            problemsPerExit.get(pick.getDestiny()).getPicks().add(pick);
        }

        //Calcular centro de massa de cada conjunto de picks (por saída)
        for(Problem oneExitProblem: problemsPerExit.values()){
            oneExitProblem.computeMassCenter();
        }

        //Atribuir conjuntos de picks aos agentes (critério: centro de massa)
        //Se houver mais agentes do que picks não há problema...
        //E se houver mais saídas do que agentes?
        //Neste caso, a escolha dos picks/saídas a resolver pode ser feita antes de se chamar o algoritmo

        List<Agent> agents = new LinkedList<>(problem.getAgents());
        for(Problem oneExitProblem: problemsPerExit.values()){
            double smallerDistanceToMassCenter = Double.MAX_VALUE;
            Agent assignedAgent = null;
            for (Agent agent : agents) {
                double distance = Math.sqrt(
                                Math.pow(agent.getInitialX() - oneExitProblem.getMassCenterX(), 2) +
                                Math.pow(agent.getInitialY() - oneExitProblem.getMassCenterY(), 2));
                if(distance < smallerDistanceToMassCenter){
                    smallerDistanceToMassCenter = distance;
                    assignedAgent = agent;
                }
            }
            agents.remove(assignedAgent);
            oneExitProblem.addAgent(assignedAgent);
        }

        Solution solution = new Solution();
        SingleOrderDyn singleOrderDyn = new SingleOrderDyn();
        for(Problem oneExitProblem: problemsPerExit.values()){
            Solution oneExitSolution = singleOrderDyn.solve(oneExitProblem);
            Agent agent = oneExitProblem.getAgents().get(0);
            solution.addRoute(agent, oneExitSolution.getRoute(agent));
        }

        return solution;
    }
}
