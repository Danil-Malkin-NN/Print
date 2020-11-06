package com.example.gravitypaint;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;


//класс MaintActivity наследуется от класса AppCompatActivity и имплементирует интерфейсы
//SeekBar.OnSeekBarChangeListener, View.OnClickListener
public class MainActivity extends AppCompatActivity
        implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    //Объявление переменных
    private CanvasView canvasView; //Холст на котором мы будем рисовать, он имеет методы для рисования
//Класс Paint содержит информацию о стиле и цвете о том, как рисовать геометрию, текст и растровые изображения.
    private Paint myPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //Размер кисти и цвет кисти.
    private int brushSize = 5;
    private int brushColor = Color.BLACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {//При создании окна
        // Инициализируем Активность.
        super.onCreate(savedInstanceState);


        //Настройка окна для скрытия минибара вверху
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Выбор разметочного файла для отрисовки
        setContentView(R.layout.activity_main);
        //Создание нового поля для рисования
        canvasView = new CanvasView(this);
        canvasView.setId(View.generateViewId());
        FrameLayout layout = findViewById(R.id.canvas_frame);
        layout.addView(canvasView);
        //Установка чёрного цвета
        myPaint.setColor(Color.BLACK);

        //Создание ползунка для толщины линии
        SeekBar bar = findViewById(R.id.brush_size);
        bar.setOnSeekBarChangeListener(this);
        //Создание кнопок с разными цветами.
        Button greenButton = findViewById(R.id.button_green);
        Button greenDarkButton = findViewById(R.id.button_green_dark);
        Button blueButton = findViewById(R.id.button_blue);
        Button blueLightButton = findViewById(R.id.button_blue_light);
        Button blueDarkButton = findViewById(R.id.button_blue_dark);
        Button whiteButton = findViewById(R.id.button_white);
        Button orangeButton = findViewById(R.id.button_orange);
        Button orangeDarkButton = findViewById(R.id.button_orange_dark);
        Button redButton = findViewById(R.id.button_red);
        //Установка слушателя для кнопок. Сработает при нажатии
        greenButton.setOnClickListener(this);
        greenDarkButton.setOnClickListener(this);
        blueButton.setOnClickListener(this);
        blueLightButton.setOnClickListener(this);
        blueDarkButton.setOnClickListener(this);
        whiteButton.setOnClickListener(this);
        orangeButton.setOnClickListener(this);
        orangeDarkButton.setOnClickListener(this);
        redButton.setOnClickListener(this);

    }

    private String getStorePath() {//Получение хранилища
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String storePath = preferences.getString(getString(R.string.setting_store_path), null);
        if (storePath == null) {
            storePath = Environment.getExternalStorageDirectory().toString();//Получаем адрес для хранения картинки

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(getString(R.string.setting_store_path), storePath);

            editor.commit();

        }
        return storePath;
    }

    private int getImageQuality() {//Получение качества изображения
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int quality = preferences.getInt(getString(R.string.settings_image_quality), -1);

        if (quality == -1) {
            quality = 95;

            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(getString(R.string.settings_image_quality), quality);
            editor.commit();
        }
        return quality;
    }

    private Bitmap.CompressFormat getImageFormat() {
        //Определяет известные форматы, в которые можно сжать растровое изображение
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String format = preferences.getString(getString(R.string.settings_image_format), null);
        if (format == null) {
            format = "png";
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(getString(R.string.settings_image_format), format);
            editor.commit();
        }
        switch (format) {
            case "png":
                return Bitmap.CompressFormat.PNG;
            case "jpeg":
                return Bitmap.CompressFormat.JPEG;
            default:
                return null; // Никогда не должно произойти
        }
    }

    private void saveImage() {
        //Сохранение картинки

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
            return;
        }

        Bitmap image = canvasView.getImage();

        String storePath = getStorePath();
        Bitmap.CompressFormat format = getImageFormat();
        String imageFileName = ImageSaver.generateImageFileName(storePath, format);
        try {

            ImageSaver.saveImage(image, format, getImageQuality(), imageFileName);
            Toast.makeText(this, "Мы оставили этот шедевр в хранилище с именем: " + imageFileName,
                    Toast.LENGTH_LONG).show();

        } catch (IOException e) {

            Log.e(MainActivity.class.getCanonicalName(),
                    e.getMessage());
            Toast.makeText(MainActivity.this, "Кажется вохникли неполадки!"
                    +e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void openSettings() {
        //Переключение на активность настроек
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Настройка настроек!)
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.menu_item_save:
                saveImage();
                break;

            case R.id.menu_item_settings:
                openSettings();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Отрисовка меню
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Обработка косаний
        super.onTouchEvent(event);
        Canvas canvas = canvasView.getCanvas();

        int[] location = new int[2];
        canvasView.getLocationOnScreen(location);

        myPaint.setColor(brushColor);
        canvas.drawCircle(event.getX(), event.getY() - location[1], brushSize, myPaint);

        canvasView.invalidate();

        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
        brushSize = i;
    }

    @Override
    public void onStartTrackingTouch(SeekBar  seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        Button button = (Button) v;
        brushColor = button.getBackgroundTintList().getDefaultColor();
    }
}