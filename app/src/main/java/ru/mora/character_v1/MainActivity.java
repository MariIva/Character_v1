package ru.mora.character_v1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // список персонажей
    ArrayList<Character> characters = new ArrayList<>();
    ListView listView;
    // путь к файлу
    String path ;
    // код запроса разрешения на запись
    public static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 2;

    public static final int REQUEST_CODE_SETTING = 1;    // код запроса на активность настроек
    public static final int REQUEST_CODE_CREATE = 2;    // код запроса на создание персонажа

    // метод устанавливающий тему активности
    public void setTheme() {
        // берем контекст приложения
        Context context = getApplicationContext();
        // получаем предпочтения всего приложения
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        // берем значение цветовой темы
        String theme = prefs.getString("view", "standart");
        // берем значение включения режима темной темы
        boolean dark = prefs.getBoolean("dark_theme", false);
        // установка нужной цветовой схемы
        if (theme.equals("costom_theme")){
            setTheme(R.style.Theme_CUSTOM_THEME_NoActionBar);
        }
        else{
            setTheme(R.style.Theme_Character_v1_NoActionBar);
        }
        // установка темной темы
        if (dark){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    // сохрание списка персонажей, как сериаливанные объекты
    public void saveCharacter()  {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
            oos.writeObject(characters);
            oos.close();
        } catch (IOException e) {
            ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.main_layout);
            //создаем Snackbar
            Snackbar snackbar = Snackbar.make(layout, "Ошибка записи в файл", Snackbar.LENGTH_LONG);
            // показываем Snackbar
            snackbar.show();
        }
    }
    // загрузка списка персонажей
    public void loadCharacter() throws IOException, ClassNotFoundException {
        // создаем описание файла
        File f = new File(path);
        // если файла не существует, то создаем его
        if (!f.exists()) {
            f.createNewFile();
        }
        FileInputStream stream = new FileInputStream(path);
        if (stream.available()>0) {
            ObjectInputStream ois = new ObjectInputStream(stream);
            characters = (ArrayList<Character>) ois.readObject();
            ois.close();
        }
    }

    public void permission()  {
        int permissionStatus_write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionStatus_write == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,     // эта активность
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},     // список размешений
                    PERMISSION_WRITE_EXTERNAL_STORAGE);   // код запроса разрешения
        }
        else{
            try {
                // считываем список персонажей из файла
                loadCharacter();
            } catch (IOException | ClassNotFoundException ex) {
                ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.main_layout);
                //создаем Snackbar
                Snackbar snackbar = Snackbar.make(layout, "Ошибка чтения файла", Snackbar.LENGTH_LONG);
                // показываем Snackbar
                snackbar.show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // путь к директории, где лежат файлы для приложения
        path = getFilesDir().toString()+"/Character.dat";

        permission();

        // создаем адаптер заполняющий список
        CharacterAdapter characterAdapter = new CharacterAdapter(this, characters);

        listView = findViewById(R.id.list_char);
        // устанавливаем адаптер
        listView.setAdapter(characterAdapter);
        // клик на элемент списка
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id){
                Toast.makeText(getApplicationContext(), "position "+position + " id "+ id,
                        Toast.LENGTH_SHORT).show();
            }
        });

        // кнопка для перехода в активность создания персонажа
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CharActivity.class);
                startActivityForResult(intent,REQUEST_CODE_CREATE);
            }
        });
    }

    // метод, создающий меню на актиквности
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    //  метод обрабатывабщий клики на пункты меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // если клик на пунк "setting", то открываем активность настроек
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivityForResult(intent,REQUEST_CODE_SETTING);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // если пользователь сменил цветовую схему, то пересоздаем активность
        if (requestCode==REQUEST_CODE_SETTING) {
            this.recreate();
        }
        // если пользователь создал персонажа
        if (requestCode==REQUEST_CODE_CREATE){
            switch (resultCode){
                case RESULT_OK:
                    // получаем персонажа как поток битов и переводим в объект
                    Character character = (Character) data.getSerializableExtra("NEW_CHARACTER");
                    // добавляем персонажа в список
                    characters.add(character);
                    // обновляем адаптер и список на экране
                    CharacterAdapter characterAdapter = new CharacterAdapter(this, characters);
                    listView.setAdapter(characterAdapter);
                    // сохраняем обновленный список персонажей
                    saveCharacter();
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // обработка ответа пользователя на запрос разрешения
        switch (requestCode) { // код запроса
            case PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    CoordinatorLayout layout = (CoordinatorLayout) findViewById(R.id.c_l_main);
                    //создаем Snackbar
                    Snackbar snackbar = Snackbar.make(layout, "Ваши данные не будут сохраняться", Snackbar.LENGTH_LONG);
                    // показываем Snackbar
                    snackbar.show();
                }
                break;
        }
    }

    class CharacterAdapter extends BaseAdapter {
        LayoutInflater lInflater;
        ArrayList<Character> objects;

        CharacterAdapter(Context context, ArrayList<Character> craracter) {
            objects = craracter;
            lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        // кол-во элементов
        @Override
        public int getCount() {
            return objects.size();
        }

        // элемент по позиции
        @Override
        public Object getItem(int position) {
            return objects.get(position);
        }

        // id по позиции
        @Override
        public long getItemId(int position) {
            return position;
        }

        // пункт списка
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // используем созданные, но не используемые view
            View view = convertView;
            if (view == null) {
                view = lInflater.inflate(R.layout.list_row, parent, false);
            }

            Character character = (Character) getItem(position);

            // заполняем View в пункте списка данными
            ((TextView) view.findViewById(R.id.list_tv_name)).setText(character.name);
            ((TextView) view.findViewById(R.id.list_tv_race)).setText(character.race);
            if (character.img_uri.equals("")){
                ((ImageView) view.findViewById(R.id.list_img_icon)).setImageResource(R.drawable.char_icon);
            }
            else {
                    Uri uri = Uri.parse(character.img_uri);
                    ((ImageView) view.findViewById(R.id.list_img_icon)).setImageURI(uri);
            }
            return view;
        }



    }
}