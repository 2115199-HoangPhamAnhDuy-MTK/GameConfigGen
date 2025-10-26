package me.etylix.configgen;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ConfigGenApp";
    private static final String CONFIG_FILENAME = "config.txt";

    private TextView tvSocValue, tvTierValue, tvConfigContent;
    private EditText etSocOverride;
    private Button btnUpdate;

    private ConfigGenerator configGenerator;
    private SocDatabaseHelper dbHelper;
    private File configFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new SocDatabaseHelper(this);
        configGenerator = new ConfigGenerator();

        configFile = new File(this.getFilesDir(), CONFIG_FILENAME);

        // Ánh xạ View
        tvSocValue = findViewById(R.id.tvSocValue);
        tvTierValue = findViewById(R.id.tvTierValue);
        tvConfigContent = findViewById(R.id.tvConfigContent);
        etSocOverride = findViewById(R.id.etSocOverride);
        btnUpdate = findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String overrideSoc = etSocOverride.getText().toString().trim();
                if (!overrideSoc.isEmpty()) {
                    processDeviceConfig(overrideSoc);
                } else {
                    processDeviceConfig(null);
                }
            }
        });
        processDeviceConfig(null);
    }

    private void processDeviceConfig(String overrideSoc) {
        String currentSoc;
        if (overrideSoc != null && !overrideSoc.isEmpty()) {
            currentSoc = overrideSoc;
        } else {
            currentSoc = getDeviceSocModel();
        }

        tvSocValue.setText(currentSoc);

        String tier = dbHelper.getTierForSoc(currentSoc);
        tvTierValue.setText(tier);

        GraphicsStrategy strategy = getStrategyForTier(tier);
        configGenerator.setStrategy(strategy);

        String fileContent = configGenerator.generateConfigFileContent();
        writeFile(fileContent);
        readFileAndDisplay();
    }

    private void writeFile(String content) {
        try (FileOutputStream fos = new FileOutputStream(configFile);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos))) {
            writer.write(content);
        } catch (IOException e) {
            Log.e(TAG, "Lỗi khi ghi file", e);
            Toast.makeText(this, "Lỗi khi ghi file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void readFileAndDisplay() {
        if (!configFile.exists()) {
            tvConfigContent.setText("File " + CONFIG_FILENAME + " không tồn tại.");
            return;
        }

        StringBuilder text = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(configFile);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {

            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line).append('\n');
            }
            tvConfigContent.setText(text.toString());
            Log.i(TAG, "Đã đọc file config.txt thành công.");

        } catch (IOException e) {
            Log.e(TAG, "Lỗi khi đọc file", e);
            tvConfigContent.setText("Lỗi khi đọc file: " + e.getMessage());
        }
    }
    private String getDeviceSocModel() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                // Android 14
                return Build.SOC_MODEL;
            } else {
                // reflection for < API34
                @SuppressLint("PrivateApi") Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
                Method getMethod = systemPropertiesClass.getMethod("get", String.class);
                String socModel = (String) getMethod.invoke(null, "ro.soc.model");
                if (socModel != null && !socModel.isEmpty()) {
                    return socModel.toUpperCase();
                } else {
                    return Build.HARDWARE.toUpperCase();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Không thể đọc ro.soc.model", e);
            return Build.HARDWARE.toUpperCase();
        }
    }

    private GraphicsStrategy getStrategyForTier(String tier) {
        switch (tier.toLowerCase()) {
            case "high":
                return new HighEndStrategy();
            case "medplus":
                return new HigherMidRangeStrategy();
            case "med":
                return new MidRangeStrategy();
            case "low":
            default:
                return new LowEndStrategy();
        }
    }
}

