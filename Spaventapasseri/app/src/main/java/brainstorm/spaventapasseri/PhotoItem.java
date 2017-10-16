package brainstorm.spaventapasseri;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


enum SortMode
{
    byTitle,
    byAmount,
    byDate
}

class PhotoItem implements Serializable
{
    //public static final

    private static class PIComps
    {
        static final Comparator<PhotoItem> byTitle = new Comparator<PhotoItem>() {
            @Override
            public int compare(PhotoItem p1, PhotoItem p2) {
                return p1.getTitle().compareTo(p2.getTitle());
            }
        };

        static final Comparator<PhotoItem> byAmount = new Comparator<PhotoItem>() {
            @Override
            public int compare(PhotoItem p1, PhotoItem p2) {
                return p1.getAmount().compareTo(p2.getAmount());
            }
        };

        static final Comparator<PhotoItem> byDate = new Comparator<PhotoItem>() {
            @Override
            public int compare(PhotoItem p1, PhotoItem p2) {
                return -p1.getDate().compareTo(p2.getDate()); //R  il segno meno e' per invertire l'ordine
            }
        };
    }

    static void sort(List<PhotoItem> list, SortMode mode)
    {
        switch (mode)
        {
            case byTitle:
                Collections.sort(list, PIComps.byTitle);
                break;
            case byAmount:
                Collections.sort(list, PIComps.byAmount);
                break;
            case byDate:
            default:
                Collections.sort(list, PIComps.byDate);
                break;
        }
    }

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
