package brainstorm.spaventapasseri;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;

class PhotoItem
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
        title = file.getName();
        date = new Date(file.lastModified());
        stringDate = date.toString();
        // todo: amount
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
    }

    void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

}
