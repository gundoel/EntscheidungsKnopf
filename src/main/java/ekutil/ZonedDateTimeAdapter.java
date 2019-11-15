package ekutil;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.ZonedDateTime;

public class ZonedDateTimeAdapter extends XmlAdapter<String,ZonedDateTime> {
    @Override
    public ZonedDateTime unmarshal(String datetime) throws Exception {
        return ZonedDateTime.parse(datetime);
    }

    @Override
    public String marshal(ZonedDateTime datetime) throws Exception {
        if (datetime != null) {
            return datetime.toString();
        } else {
            return null;
        }
    }
}
