package br.com.luanyata.todolist;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button btnSalvar;
    private EditText input;
    private ListView listaTarefas;
    private SQLiteDatabase bancoDados;
    private ArrayAdapter<String> itensLista;
    private ArrayList<String> itens;
    private ArrayList<Integer> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            btnSalvar = (Button) findViewById(R.id.idBtnSalvar);
            input = (EditText) findViewById(R.id.idInput);
            listaTarefas = (ListView) findViewById(R.id.idListaTarefa);


            bancoDados = openOrCreateDatabase("apptarefas", MODE_PRIVATE, null);
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS tarefas(_id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa VARCHAR)");

            btnSalvar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String textoDigitado = input.getText().toString();
                    if (!textoDigitado.isEmpty())
                        salvarTarefa(textoDigitado);
                    else
                        Toast.makeText(getApplicationContext(), "O campo está em branco", Toast.LENGTH_LONG).show();
                }
            });

            listaTarefas.setLongClickable(true);
            listaTarefas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int posicao, long l) {
                    removerTarefa(ids.get(posicao));
                    return true;
                }
            });
            recuperarTarefas();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void salvarTarefa(String tarefa) {
        try {
            bancoDados.execSQL("INSERT INTO tarefas(tarefa) VALUES('" + tarefa + "')");
            Toast.makeText(getApplicationContext(), "Tarefa salva com sucesso", Toast.LENGTH_SHORT).show();
            input.setText("");
            recuperarTarefas();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void recuperarTarefas() {
        try {
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM tarefas ORDER BY _id DESC", null);
            int indiceColunaId = cursor.getColumnIndex("_id");
            int indiceColunaTarefa = cursor.getColumnIndex("tarefa");


            itens = new ArrayList<>();
            ids = new ArrayList<>();

            itensLista = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, android.R.id.text1, itens);

            listaTarefas.setAdapter(itensLista);

            cursor.moveToFirst();
            while (cursor != null) {
                Log.i("RESULTADO - ", "Tarefa: " + cursor.getString(indiceColunaTarefa));

                itens.add(cursor.getString(indiceColunaTarefa));
                ids.add(Integer.parseInt(cursor.getString(indiceColunaId)));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removerTarefa(Integer id) {
        try {
            bancoDados.execSQL("DELETE FROM tarefas WHERE _id=" + id);
            recuperarTarefas();
            Toast.makeText(getApplicationContext(), "Tarefa Removida com Sucesso", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}
