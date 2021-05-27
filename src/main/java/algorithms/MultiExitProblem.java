package algorithms;

import java.util.List;

public class MultiExitProblem extends Problem{

    private List<OneExitProblem> problems;

    public MultiExitProblem(List<OneExitProblem> problems) {
        this.problems = problems;
    }

    public List<OneExitProblem> getProblems() {
        return problems;
    }

    public void setProblems(List<OneExitProblem> problems) {
        this.problems = problems;
    }
}
