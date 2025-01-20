package dev.batugokce;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImzaSonucu {
    private String imzaliVeriDosyaYolu;
    private String imzaciAdSoyad;
}
