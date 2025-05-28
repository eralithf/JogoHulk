package com.example.jogohulk;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jogohulk.R;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private GridLayout gridHulks;
    private TextView tvPontos;
    private ProgressBar progressBar;
    private Button btnStart;

    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean jogoAtivo = false;
    private int pontos = 0;
    private int tempo = 100;
    private final int DURACAO_JOGO = 30000;

    private ImageView[] imagens;
    private int[] hulkDrawables = {
            R.drawable.hulk_01,
            R.drawable.hulk_02,
            R.drawable.hulk_03
    };

    private Runnable engineRunnable;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridHulks = findViewById(R.id.gridHulks);
        tvPontos = findViewById(R.id.tvPontos);
        progressBar = findViewById(R.id.progressBar);
        btnStart = findViewById(R.id.btnStart);

        criarImagens();

        btnStart.setOnClickListener(v -> iniciarJogo());
    }

    private void criarImagens() {
        imagens = new ImageView[9];
        for (int i = 0; i < 9; i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(new GridLayout.LayoutParams());
            iv.setAdjustViewBounds(true);
            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
            iv.setVisibility(View.INVISIBLE); // começa escondido

            int finalI = i;
            iv.setOnClickListener(v -> {
                if (jogoAtivo && iv.getVisibility() == View.VISIBLE) {
                    pontos++;
                    tvPontos.setText("Pontos: " + pontos);
                    iv.setVisibility(View.INVISIBLE);
                }
            });

            imagens[i] = iv;
            gridHulks.addView(iv);
        }
    }

    private void iniciarJogo() {
        jogoAtivo = true;
        pontos = 0;
        tempo = 100;
        tvPontos.setText("Pontos: 0");
        progressBar.setProgress(tempo);

        btnStart.setEnabled(false);

        startTempo();
        startEngine();
    }

    private void startTempo() {
        new Thread(() -> {
            while (tempo > 0 && jogoAtivo) {
                tempo--;
                runOnUiThread(() -> progressBar.setProgress(tempo));
                try {
                    Thread.sleep(DURACAO_JOGO / 100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            runOnUiThread(() -> encerrarJogo());
        }).start();
    }

    private void startEngine() {
        engineRunnable = new Runnable() {
            @Override
            public void run() {
                if (!jogoAtivo) return;

                esconderTodos();
                mostrarHulkAleatorio();

                handler.postDelayed(this, 500); // muda a cada 0.5s
            }
        };
        handler.post(engineRunnable);
    }

    private void esconderTodos() {
        for (ImageView img : imagens) {
            img.setVisibility(View.INVISIBLE);
        }
    }

    private void mostrarHulkAleatorio() {
        int index = random.nextInt(imagens.length);
        int hulk = hulkDrawables[random.nextInt(hulkDrawables.length)];

        imagens[index].setImageResource(hulk);
        imagens[index].setVisibility(View.VISIBLE);
    }

    private void encerrarJogo() {
        jogoAtivo = false;
        handler.removeCallbacks(engineRunnable);
        esconderTodos();
        Toast.makeText(this, "Fim de jogo! Pontuação: " + pontos, Toast.LENGTH_LONG).show();
        btnStart.setEnabled(true);
    }
}
