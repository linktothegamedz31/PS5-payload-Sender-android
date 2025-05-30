package com.linktothe.ps5payloadsender;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.OpenableColumns;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;

public class PrimaryPayloadsFragment extends Fragment {

    private MainActivity mainActivity;
    private TextView selectedFileTextView;
    private Button sendCustomPayloadButton;
    private Uri selectedFileUri;
    private static final int FILE_SELECT_CODE = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_primary_payloads, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        mainActivity = (MainActivity) getActivity();
        
        // Set up primary payload buttons
        setupPayloadButton(view, R.id.btn_send_etahen, "primary_payloads/etaHEN.bin");
        setupPayloadButton(view, R.id.btn_send_kstuff, "primary_payloads/kstuff.elf");
        setupPayloadButton(view, R.id.btn_send_umtx1, "primary_payloads/umtx1.jar");
        setupPayloadButton(view, R.id.btn_send_elfloader, "primary_payloads/elfloader.jar");
        setupPayloadButton(view, R.id.btn_send_umtx2, "primary_payloads/umtx2.jar");
        setupPayloadButton(view, R.id.btn_send_websrv, "primary_payloads/websrv.elf");
        setupPayloadButton(view, R.id.btn_send_kstuff_toggle, "primary_payloads/kstuff_toggle.elf");
        
        // Set up custom payload section
        selectedFileTextView = view.findViewById(R.id.tv_selected_file);
        sendCustomPayloadButton = view.findViewById(R.id.btn_send_custom_payload);
        
        Button selectFileButton = view.findViewById(R.id.btn_select_custom_payload);
        selectFileButton.setOnClickListener(v -> selectFile());
        
        sendCustomPayloadButton.setOnClickListener(v -> {
            if (selectedFileUri != null) {
                mainActivity.sendCustomPayload(selectedFileUri);
            }
        });
    }
    
    public void setupPayloadButton(View view, int buttonId, String payloadPath) {
        Button button = view.findViewById(buttonId);
        if (button != null) {
            button.setOnClickListener(v -> mainActivity.sendPayload(payloadPath));
        }
    }
    
    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, FILE_SELECT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_SELECT_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                selectedFileUri = data.getData();
                if (selectedFileUri != null) {
                    String fileName = getFileNameFromUri(selectedFileUri);
                    selectedFileTextView.setText(fileName);
                    sendCustomPayloadButton.setEnabled(true);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getFileNameFromUri(Uri uri) {
        String fileName = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = null;
            try {
                cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (fileName == null) {
            fileName = uri.getPath();
            int cut = fileName.lastIndexOf('/');
            if (cut != -1) {
                fileName = fileName.substring(cut + 1);
            }
        }
        return fileName;
    }


    }




