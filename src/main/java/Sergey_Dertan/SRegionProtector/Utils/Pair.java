package Sergey_Dertan.SRegionProtector.Utils;

import java.util.Objects;

public final class Pair<F, S> {

    public F key;
    public S value;

    public Pair(F key, S value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Pair) {
            Pair pair = (Pair) obj;
            return Objects.equals(this.key, pair.key) && Objects.equals(this.value, pair.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (this.key != null ? this.key.hashCode() : 0) * 13 + (this.value != null ? this.value.hashCode() : 0);
    }
}
