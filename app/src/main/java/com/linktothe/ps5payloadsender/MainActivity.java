package com.linktothe.ps5payloadsender;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "PS5PayloadSender";
    private SharedPreferences preferences;
    
    private EditText ipAddressField;
    private EditText portField;
    private TextView statusText;
    private static final int STORAGE_PERMISSION_CODE = 101;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkStoragePermissions()) {
            copyPayloadFilesIfNeeded();
        } else {
            requestStoragePermissions();
        }

        // Initialize preferences
        preferences = getSharedPreferences("PS5PayloadSender", MODE_PRIVATE);
        
        // Initialize UI elements
        ipAddressField = findViewById(R.id.ip_address);
        portField = findViewById(R.id.port);
        statusText = findViewById(R.id.status_text);
        
        // Load saved configuration
        loadConfig();
        
        // Copy payload files if needed
        copyPayloadFilesIfNeeded();
        
        // Set up save config button
        Button saveConfigButton = findViewById(R.id.btn_save_config);
        saveConfigButton.setOnClickListener(v -> saveConfig());
        
        // Set up ViewPager and TabLayout
        setupViewPager();
    }
    
    private void setupViewPager() {
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.tab_primary_payloads);
                    break;
                case 1:
                    tab.setText(R.string.tab_secondary_payloads);
                    break;
            }
        }).attach();
    }

    private boolean checkStoragePermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED;
        } else {
            // Permissions are granted at install time on older versions
            return true;
        }
    }
    
    private void requestStoragePermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage Permissions Granted", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Storage permission granted, attempting to copy files.");
                copyPayloadFilesIfNeeded();
            } else {
                Toast.makeText(this, "Storage Permissions Denied. Payloads cannot be copied.", Toast.LENGTH_LONG).show();
                // Handle the case where permission is denied, e.g., disable functionality
            }
        }
    }
    
    private void loadConfig() {
        String ip = preferences.getString("ip", "192.168.1.1");
        int port = preferences.getInt("port", 9021);
        
        ipAddressField.setText(ip);
        portField.setText(String.valueOf(port));
        
        Log.i(TAG, "Configuration loaded: IP=" + ip + ", Port=" + port);
    }
    
    private void saveConfig() {
        try {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("ip", ipAddressField.getText().toString());
            editor.putInt("port", Integer.parseInt(portField.getText().toString()));
            
            // Save payload paths (would need to implement if you allow changing paths)
            // For now, we just save the IP and port
            
            editor.apply();
            Toast.makeText(this, "Configuration saved successfully", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Configuration saved successfully");
        } catch (Exception e) {
            String errorMsg = "Failed to save configuration: " + e.getMessage();
            Log.e(TAG, errorMsg, e);
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
        }
    }
    
    private void copyPayloadFilesIfNeeded() {
        // Create directories for payloads
        File primaryDir = new File(getExternalFilesDir(null), "primary_payloads");
        Log.d(TAG, "Primary payload directory target: " + primaryDir.getAbsolutePath());
        primaryDir.mkdirs();

        File henDir = new File(getExternalFilesDir(null), "etaHen_Payloads");
        File elfDir = new File(getExternalFilesDir(null), "elf_payloads");
        
        primaryDir.mkdirs();
        henDir.mkdirs();
        elfDir.mkdirs();
        Log.d(TAG, "Primary dir created: " + primaryDir.mkdirs());
        Log.d(TAG, "Hen dir created: " + henDir.mkdirs());
        Log.d(TAG, "Elf dir created: " + elfDir.mkdirs());
        // Copy assets to external storage
        // List of all payload files to copy
        String[][] assetsToCopy = {
            // Primary payloads
            {"primary_payloads/etaHEN.bin", "primary_payloads/etaHEN.bin"},
            {"primary_payloads/kstuff.elf", "primary_payloads/kstuff.elf"},
            {"primary_payloads/umtx1.jar", "primary_payloads/umtx1.jar"},
            {"primary_payloads/elfloader.jar", "primary_payloads/elfloader.jar"},
            {"primary_payloads/umtx2.jar", "primary_payloads/umtx2.jar"},
            {"primary_payloads/websrv.elf", "primary_payloads/websrv.elf"},
            {"primary_payloads/kstuff_toggle.elf", "primary_payloads/kstuff_toggle.elf"},
            
            // HEN payloads
            {"etaHen_Payloads/etaHEN14.bin", "etaHen_Payloads/etaHEN14.bin"},
            {"etaHen_Payloads/etaHEN15.bin", "etaHen_Payloads/etaHEN15.bin"},
            {"etaHen_Payloads/etaHEN16.bin", "etaHen_Payloads/etaHEN16.bin"},
            {"etaHen_Payloads/etaHEN17.bin", "etaHen_Payloads/etaHEN17.bin"},
            {"etaHen_Payloads/etaHEN18.bin", "etaHen_Payloads/etaHEN18.bin"},
            {"etaHen_Payloads/etaHEN19.bin", "etaHen_Payloads/etaHEN19.bin"},
            
            // ELF payloads
            {"elf_payloads/airpsx.elf", "elf_payloads/airpsx.elf"},
            {"elf_payloads/BackupDB.elf", "elf_payloads/BackupDB.elf"},
            {"elf_payloads/BackupDbUser.elf", "elf_payloads/BackupDbUser.elf"},
            {"elf_payloads/ftpsrv.elf", "elf_payloads/ftpsrv.elf"},
            {"elf_payloads/mp4dumper.elf", "elf_payloads/mp4dumper.elf"},
            {"elf_payloads/ps5-self-decrypter.elf", "elf_payloads/ps5-self-decrypter.elf"}
        };
        
        for (String[] asset : assetsToCopy) {
            try {
                File outFile = new File(getExternalFilesDir(null), asset[1]);
                if (!outFile.exists()) {
                    InputStream in = getAssets().open(asset[0]);
                    FileOutputStream out = new FileOutputStream(outFile);
                    
                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    
                    in.close();
                    out.flush();
                    out.close();
                    Log.d(TAG, "Copied asset: " + asset[0]);
                }
            } catch (IOException e) {
                Log.e(TAG, "Failed to copy asset: " + asset[0], e);
            }
        }
    }
    
    public void sendPayload(String payloadPath) {
        String ip = ipAddressField.getText().toString();
        int port;
        
        // Use port 9025 specifically for umtx1, umtx2, and elfloader
        if (payloadPath.contains("umtx1.jar") || 
            payloadPath.contains("umtx2.jar") || 
            payloadPath.contains("elfloader.jar")) {
            port = 9025;
        } else {
            try {
                port = Integer.parseInt(portField.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid port number", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        File payloadFile = new File(getExternalFilesDir(null), payloadPath);
        if (!payloadFile.exists()) {
            Toast.makeText(this, "Payload file not found: " + payloadPath, Toast.LENGTH_SHORT).show();
            return;
        }
        
        updateStatus("Sending payload: " + payloadPath + " to " + ip + ":" + port);
        
        // Execute payload sending in background
        new SendPayloadTask(ip, port, payloadFile).execute();
    }
    
    public void sendCustomPayload(Uri fileUri) {
        String ip = ipAddressField.getText().toString();
        int port;
        
        try {
            port = Integer.parseInt(portField.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid port number", Toast.LENGTH_SHORT).show();
            return;
        }
        
        updateStatus("Sending custom payload to " + ip + ":" + port);
        
        // Execute payload sending in background
        new SendCustomPayloadTask(ip, port, fileUri).execute();
    }
    
    private void updateStatus(String message) {
        runOnUiThread(() -> {
            statusText.setText(message);
            Log.i(TAG, message);
        });
    }
    
    private class SendPayloadTask extends AsyncTask<Void, String, Boolean> {
        private final String ip;
        private final int port;
        private final File payloadFile;
        private String errorMessage;
        
        public SendPayloadTask(String ip, int port, File payloadFile) {
            this.ip = ip;
            this.port = port;
            this.payloadFile = payloadFile;
        }
        
        @Override
        protected void onProgressUpdate(String... values) {
            if (values.length > 0) {
                updateStatus(values[0]);
            }
        }
        
        @Override
        protected Boolean doInBackground(Void... voids) {
            Socket socket = null;
            OutputStream out = null;
            InputStream in = null;
            
            try {
                publishProgress("Connecting to PS5 at " + ip + ":" + port);
                socket = new Socket(ip, port);
                
                publishProgress("Connected. Sending payload: " + payloadFile.getName());
                out = socket.getOutputStream();
                in = new java.io.FileInputStream(payloadFile);
                
                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalSent = 0;
                long fileSize = payloadFile.length();
                
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    totalSent += bytesRead;
                    
                    // Update progress every 10%
                    if (totalSent % (fileSize / 10) < 4096) {
                        int progress = (int)((totalSent * 100) / fileSize);
                        publishProgress("Sending payload: " + progress + "% complete");
                    }
                }
                
                out.flush();
                publishProgress("Payload sent successfully!");
                return true;
                
            } catch (Exception e) {
                errorMessage = "Error sending payload: " + e.getMessage();
                Log.e(TAG, errorMessage, e);
                return false;
            } finally {
                try {
                    if (in != null) in.close();
                    if (out != null) out.close();
                    if (socket != null) socket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing resources", e);
                }
            }
        }
        
        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(MainActivity.this, "Payload sent successfully", Toast.LENGTH_SHORT).show();
            } else {
                updateStatus(errorMessage);
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private class SendCustomPayloadTask extends AsyncTask<Void, String, Boolean> {
        private final String ip;
        private final int port;
        private final Uri fileUri;
        private String errorMessage;
        
        public SendCustomPayloadTask(String ip, int port, Uri fileUri) {
            this.ip = ip;
            this.port = port;
            this.fileUri = fileUri;
        }
        
        @Override
        protected void onProgressUpdate(String... values) {
            if (values.length > 0) {
                updateStatus(values[0]);
            }
        }
        
        @Override
        protected Boolean doInBackground(Void... voids) {
            Socket socket = null;
            OutputStream out = null;
            InputStream in = null;
            
            try {
                publishProgress("Connecting to PS5 at " + ip + ":" + port);
                socket = new Socket(ip, port);
                
                publishProgress("Connected. Sending custom payload");
                out = socket.getOutputStream();
                in = getContentResolver().openInputStream(fileUri);
                
                if (in == null) {
                    throw new IOException("Could not open input stream for custom payload");
                }
                
                byte[] buffer = new byte[4096];
                int bytesRead;
                
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                
                out.flush();
                publishProgress("Custom payload sent successfully!");
                return true;
                
            } catch (Exception e) {
                errorMessage = "Error sending custom payload: " + e.getMessage();
                Log.e(TAG, errorMessage, e);
                return false;
            } finally {
                try {
                    if (in != null) in.close();
                    if (out != null) out.close();
                    if (socket != null) socket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing resources", e);
                }
            }
        }
        
        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(MainActivity.this, "Custom payload sent successfully", Toast.LENGTH_SHORT).show();
            } else {
                updateStatus(errorMessage);
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }
}
