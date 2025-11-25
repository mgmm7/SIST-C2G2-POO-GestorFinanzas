package pe.edu.upeu.gestorfinanciero.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class CurrencyService {

    private final Map<String, Double> rates = new HashMap<>();

    public CurrencyService() {
        // tasas fijas
        rates.put("PEN", 1.0);
        rates.put("USD", 0.26);  // 1 PEN = 0.26 USD
        rates.put("EUR", 0.24);  // 1 PEN = 0.24 EUR
    }

    /**
     * Convierte monto desde PEN a la moneda destino.
     */
    public double convertFromPen(double montoPen, String monedaDestino) {
        monedaDestino = monedaDestino.toUpperCase();
        return montoPen * rates.getOrDefault(monedaDestino, 1.0);
    }

    /**
     * Convierte monto DESDE monedaDestino HACIA PEN.
     * Ej: montoUSD / 0.26 = montoPEN
     */
    public double convertToPen(double monto, String monedaOrigen) {
        monedaOrigen = monedaOrigen.toUpperCase();
        double rate = rates.getOrDefault(monedaOrigen, 1.0);
        return monto / rate;
    }

    public Map<String, Double> getRates() {
        return rates;
    }
}
