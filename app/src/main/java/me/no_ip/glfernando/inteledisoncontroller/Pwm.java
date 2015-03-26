package me.no_ip.glfernando.inteledisoncontroller;

import java.io.Serializable;

/**
 * Created by fernando on 3/24/15.
 */
public class Pwm implements Serializable {
    public int pin;
    public int period;
    public boolean servo;
    public float value;

    Pwm(int pin, int period, boolean servo, float value) {
        this.pin = pin;
        this.period = period;
        this.servo = servo;
        this.value = value;
    }
}
