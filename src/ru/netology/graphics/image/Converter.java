package ru.netology.graphics.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;


public class Converter implements TextGraphicsConverter {
    protected int maxWidth;
    protected int maxHeight;
    protected double maxRatio;
    protected TextColorSchema schema = new Schema();


    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        // Скачиваем картинку из интернета
        BufferedImage img = ImageIO.read(new URL(url));

        // Получаем ширину и высоту картинки
        int currWidth = img.getWidth();
        int currHeight = img.getHeight();
        // Если конвертер попросили проверять на максимально допустимое
        // соотношение сторон изображения, то здесь выполняется эту проверка,
        // если картинка не подходит, выбрасывается исключение
        if (maxRatio > 0) {
            double dWidth = currWidth;
            double dHeight = currHeight;
            double ratio = dWidth / dHeight;
            if (ratio > maxRatio) {
                throw new BadImageSizeException(ratio, maxRatio);
            } else if (ratio < (1 / maxRatio)) {
                throw new BadImageSizeException(ratio, maxRatio);
            }
        }
        // Если конвертеру выставили максимально допустимые ширину и/или высоту,
        // то по ним и по текущим высоте и ширине вычисляются новые высота
        // и ширина.
        // Соблюдение пропорций означает, что ширина и высота уменьшаются
        // в одинаковое количество раз.
        if (maxWidth > 0) {
            if (currWidth > maxWidth) {
                currHeight = currHeight * maxWidth / currWidth;
                currWidth = maxWidth;
            }
        }
        if (maxHeight > 0) {
            if (currHeight > maxHeight) {
                currWidth = currWidth * maxHeight / currHeight;
                currHeight = maxHeight;
            }
        }
        int newHeight = currHeight;
        int newWidth = currWidth;

        // Масштабирование картинки на новые размеры
        // Последний параметр означает, что мы просим картинку плавно сузиться
        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);
        // Сделаем картинку чёрно-белой: создадим новую пустую картинку нужных размеров,
        // указав последним параметром чёрно-белую цветовую палитру
        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        // Попросим у этой картинки инструмент для рисования на ней
        Graphics2D graphics = bwImg.createGraphics();
        // Инструмент копирует масштабированное изображение
        graphics.drawImage(scaledImage, 0, 0, null);

        // Для прохода по пикселям нам нужен будет этот инструмент:
        WritableRaster bwRaster = bwImg.getRaster();
        //  Метод getPixel(w, h, new int[3]) принимает пустой, и возвращает заполненный массив из трёх интов
        //  (интенсивность красного, зелёного и синего). Но у нашей чёрно-белой картинки цветов нет,
        //  и нас интересует только первое значение в массиве.
        // На каждой внутренней итерации цикла получим степень белого пикселя (int color) и
        // конвертируем её в соответствующий символ c. Запомним символ в двумерном массиве
        int[] tempArr = new int[3];
        int arrWidth = bwRaster.getWidth();
        int arrHeight = bwRaster.getHeight();
        char[][] charArray = new char[arrHeight][arrWidth];
        for (int h = 0; h < arrHeight; h++) {
            for (int w = 0; w < arrWidth; w++) {
                int color = bwRaster.getPixel(w, h, tempArr)[0];
                char c = schema.convert(color);
                charArray[h][w] = c;
            }
        }
        // Собирём все символы в один большой текст
        // Для того, чтобы изображение не было слишком узким, каждый пиксель превращается в два повторяющихся символа
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < charArray.length; i++) {
            for (char elem : charArray[i]) {
                text.append(elem);
                text.append(elem);
            }
            text.append("\n");
        }
        return text.toString();
    }

    @Override
    public void setMaxWidth(int width) {
        maxWidth = width;
    }

    @Override
    public void setMaxHeight(int height) {
        maxHeight = height;
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema schema) {
        this.schema = schema;
    }
}



