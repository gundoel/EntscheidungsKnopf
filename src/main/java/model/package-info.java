@XmlJavaTypeAdapters({
        @XmlJavaTypeAdapter(type= ZonedDateTime.class,
                value= ZonedDateTimeAdapter.class)
})
package model;

import ekutil.ZonedDateTimeAdapter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import java.time.ZonedDateTime;