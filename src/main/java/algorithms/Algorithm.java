package algorithms;

import arwdatastruct.Task;

public abstract class Algorithm <T extends Task>{

    public abstract Solution solve(T task);
}
