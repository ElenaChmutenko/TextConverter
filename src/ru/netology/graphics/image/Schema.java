package ru.netology.graphics.image;

public class Schema implements TextColorSchema {

    protected char[] symbol = {'●', '◍', '◎', '○', '☉', '◌', '+', '-', ':'};
    protected int div = 30;

    @Override
    public char convert(int color) {
        int index = color / div;
        return symbol[index];
    }

}
