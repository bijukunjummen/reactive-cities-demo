package samples.geo.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name="cities")
public class City implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String country;
    private long pop;

    public City() {
    }

    public City(String name, String country, long pop) {
        this.name = name;
        this.country = country;
        this.pop = pop;
    }

    public City(Long id, String name, String country, long pop) {
        this(name, country, pop);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public long getPop() {
        return pop;
    }

    public void setPop(long pop) {
        this.pop = pop;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City city = (City) o;
        return pop == city.pop &&
                Objects.equals(id, city.id) &&
                Objects.equals(name, city.name) &&
                Objects.equals(country, city.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, country, pop);
    }

    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", pop=" + pop +
                '}';
    }
}