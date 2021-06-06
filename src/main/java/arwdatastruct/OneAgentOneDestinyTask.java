package arwdatastruct;

import orderpicking.Request;

public class OneAgentOneDestinyTask extends Task{

    private String destiny;

    public OneAgentOneDestinyTask(Request request, String destiny) {
        super(request);
        this.destiny = destiny;
    }

    public String getDestiny() {
        return destiny;
    }
}
