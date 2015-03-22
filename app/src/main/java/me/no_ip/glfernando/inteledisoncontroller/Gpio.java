package me.no_ip.glfernando.inteledisoncontroller;

import java.io.Serializable;

/**
 * Created by fernando on 3/13/15.
 * Gpio Class
 */
public class Gpio implements Serializable{
    public int gpio;
    public int pin;
    public boolean out;
    public int value;

    Gpio(int gpio, int pin, boolean out, int value) {
        this.gpio = gpio;
        this.pin = pin;
        this.out = out;
        this.value = value;
    }
}
