package WHDataStruct;


import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class XMLInfo {
    Date created;
    Date modified;
    boolean updating;

    public XMLInfo(String created, String modified, Byte updating) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

            this.created = format.parse(created);

            this.modified = format.parse(modified);
            this.updating = updating != 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public XMLInfo() {

    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
    public void setCreated(String created) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

            this.created = format.parse(created);
        }catch (Exception e){
            e.printStackTrace();

        }
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }
    public void setModified(String modified) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

            this.modified = format.parse(modified);
        }catch (Exception e){
            e.printStackTrace();
        }    }

    public boolean isUpdating() {
        return updating;
    }

    public void setUpdating(boolean updating) {
        this.updating = updating;
    }
    public void setUpdating(byte updating) {
        this.updating = updating != 0;
    }

    @Override
    public String toString() {
        return "XML Info" + "\n\t" + created.toString() + "\n\t" + modified.toString() + "\n\t" + "Updating : " + updating;
    }
}
