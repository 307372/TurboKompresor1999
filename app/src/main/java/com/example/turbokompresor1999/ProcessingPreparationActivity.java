package com.example.turbokompresor1999;

import static com.example.turbokompresor1999.ArchiveStructureDetailsActivity.getBlockSizeFromFlags;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import java.nio.ByteBuffer;
import java.util.BitSet;

public class ProcessingPreparationActivity extends AppCompatActivity {

    CheckBox cb_bwt;
    CheckBox cb_mtf;
    CheckBox cb_rle;

    Spinner spinnerEntropyCoding;
    Spinner spinnerHashing;

    String path;
    long filesize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing_settings);

        Bundle extras = getIntent().getExtras();
        path = extras.getString("pathToFile");
        java.io.File file = new java.io.File(path);
        filesize = file.length();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Compression");
        actionBar.setSubtitle(file.getName());
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        cb_bwt = findViewById(R.id.checkBWT);
        cb_mtf = findViewById(R.id.checkMTF);
        cb_rle = findViewById(R.id.checkRLE);

        spinnerEntropyCoding = findViewById(R.id.spinnerEntropySelector);
        ArrayAdapter<CharSequence> entropyAdapter = ArrayAdapter.createFromResource(this, R.array.entropy_codings, android.R.layout.simple_spinner_item);
        entropyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEntropyCoding.setAdapter(entropyAdapter);
        spinnerEntropyCoding.setSelection(3);

        spinnerHashing = findViewById(R.id.spinnerChecksumSelector);
        ArrayAdapter<CharSequence> hashingAdapter = ArrayAdapter.createFromResource(this, R.array.hash_algorithms, android.R.layout.simple_spinner_item);
        hashingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHashing.setAdapter(hashingAdapter);
        spinnerHashing.setSelection(3);
    }

    public BitSet getFlagsFromSelectedOptions() {
        BitSet flags = new BitSet();
        if (cb_bwt.isChecked()) flags.set(File.Flags.BWT);
        if (cb_mtf.isChecked()) flags.set(File.Flags.MTF);
        if (cb_rle.isChecked()) flags.set(File.Flags.RLE);

        String selectedEntropy = spinnerEntropyCoding.getSelectedItem().toString();
        switch (selectedEntropy) {
            case "Arithmetic coding (faster)":
                flags.set(File.Flags.AC_order0);
                break;
            case "Arithmetic coding (Slower, best compression)":
                flags.set(File.Flags.AC_order1);
                break;
            case "Asymmetric Numeral Systems (fastest)":
                flags.set(File.Flags.rANS);
                break;
        }

        String selectedHashing = spinnerHashing.getSelectedItem().toString();
        switch (selectedHashing) {
            case "CRC-32":
                flags.set(File.Flags.CRC32);
                break;
            case "SHA-1":
                flags.set(File.Flags.SHA_1);
                break;
            case "SHA-256":
                flags.set(File.Flags.SHA_256);
                break;
        }

        return flags;
    }

    public int getIntFromFlagBitset(BitSet flags) {
        long longer = flags.toLongArray()[0];
        int flagsInt = (int) longer;
        return flagsInt;
    }

    public long getAmountOfBlocks(BitSet flags) {
        return Math.round(Math.ceil((double) filesize / (double) getBlockSizeFromFlags(flags)));
    }

    public long getHowMuchProgressIs100Percent(BitSet flagSet)
    {
        int maxProgress = 0;

        if (cb_bwt.isChecked()) ++maxProgress;
        if (cb_mtf.isChecked()) ++maxProgress;
        if (cb_rle.isChecked()) ++maxProgress;

        String selectedEntropy = spinnerEntropyCoding.getSelectedItem().toString();
        if (!"None".equals(selectedEntropy)) ++maxProgress;

        maxProgress *= getAmountOfBlocks(flagSet);

        String selectedHashing = spinnerHashing.getSelectedItem().toString();
        if (!"None".equals(selectedHashing)) ++maxProgress;

        return maxProgress;
    }

    // TODO: investigate why app crashes when pressing "back arrow" on empty archive

    public void doProcessing(View v)
    {
        Intent intent = new Intent(this, ProcessingActivity.class);
        BitSet flags = getFlagsFromSelectedOptions();

        intent.putExtra("pathToFile", path);
        intent.putExtra("flags", getIntFromFlagBitset(flags));
        intent.putExtra("amountOfBlocks", getAmountOfBlocks(flags));
        intent.putExtra("partialProgressFor100Percent", getHowMuchProgressIs100Percent(flags));

        startActivity(intent);
    }
}