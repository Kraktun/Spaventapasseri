package brainstorm.spaventapasseri;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

class PhotoItem implements Serializable
{
    private File file;
    private String title;
    private BigDecimal amount;
    //R  todo: usare una libreria per leggere e scrivere il valore della spesa nei metadata

    private Date date;
    private String stringDate;

    PhotoItem(File file)
    {
        this.file = file;
        title = file.getName().substring(0, file.getName().lastIndexOf("."));
        date = new Date(file.lastModified());
        stringDate = date.toString();
        //debug
        amount = new BigDecimal(3.14f);
    }

    File getFile()
    {
        return file;
    }

    boolean deleteFile()
    {
        return file.delete();
    }

    String getTitle()
    {
        return title;
    }

    boolean setTitle(String title)
    {
        return file.renameTo(new File(file.getParent(),
                        title + file.toString().substring(file.toString().lastIndexOf("."))));
    }

    Date getDate()
    {
        return date;
    }

    String getStringDate()
    {
        return stringDate;
    }

    BigDecimal getAmount()
    {
        return amount;
        //todo: savare nel file
    }

    void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

}
