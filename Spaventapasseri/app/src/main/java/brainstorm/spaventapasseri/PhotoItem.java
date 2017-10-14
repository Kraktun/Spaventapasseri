package brainstorm.spaventapasseri;

import java.io.File;
import java.util.Date;

/**
 * Created by ricca on 14-Oct-17.
 */

public class PhotoItem {
    public File photo;
    public String title;
    public Date date;
    public int thumbnail;

    PhotoItem(File photo, String title, Date date) {
        this.photo = photo;
        this.title = title;
        this.date = date;
    }
}
