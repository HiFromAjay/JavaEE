/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package openup.client.lang.util;

/**
 *
 * @author FOXCONN
 */
public class ValueSet<T> {
    private T[] valueSet;

    public T[] getValueSet() {
        return valueSet;
    }

    public void setValueSet(T[] valueSet) {
        this.valueSet = valueSet;
    }
}
