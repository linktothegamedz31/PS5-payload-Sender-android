package com.linktothe.ps5payloadsender;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SecondaryPayloadsFragment extends Fragment {

    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_secondary_payloads, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        mainActivity = (MainActivity) getActivity();
        
        // Set up etaHEN payload buttons
        setupPayloadButton(view, R.id.btn_send_hen14, "etaHen_Payloads/etaHEN14.bin");
        setupPayloadButton(view, R.id.btn_send_hen15, "etaHen_Payloads/etaHEN15.bin");
        setupPayloadButton(view, R.id.btn_send_hen16, "etaHen_Payloads/etaHEN16.bin");
        setupPayloadButton(view, R.id.btn_send_hen17, "etaHen_Payloads/etaHEN17.bin");
        setupPayloadButton(view, R.id.btn_send_hen18, "etaHen_Payloads/etaHEN18.bin");
        setupPayloadButton(view, R.id.btn_send_hen19, "etaHen_Payloads/etaHEN19.bin");
        
        // Set up ELF payload buttons
        setupPayloadButton(view, R.id.btn_send_airpsx, "elf_payloads/airpsx.elf");
        setupPayloadButton(view, R.id.btn_send_backupdb, "elf_payloads/BackupDB.elf");
        setupPayloadButton(view, R.id.btn_send_backupdbuser, "elf_payloads/BackupDbUser.elf");
        setupPayloadButton(view, R.id.btn_send_ftpsrv, "elf_payloads/ftpsrv.elf");
        setupPayloadButton(view, R.id.btn_send_mp4dumper, "elf_payloads/mp4dumper.elf");
        setupPayloadButton(view, R.id.btn_send_self_decrypter, "elf_payloads/ps5-self-decrypter.elf");
    }
    
    private void setupPayloadButton(View view, int buttonId, String payloadPath) {
        Button button = view.findViewById(buttonId);
        if (button != null) {
            button.setOnClickListener(v -> mainActivity.sendPayload(payloadPath));
        }
    }
}
