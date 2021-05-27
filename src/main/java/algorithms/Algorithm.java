package algorithms;

public abstract class Algorithm <P extends Problem>{

    public abstract Solution solve(P problem);
}
