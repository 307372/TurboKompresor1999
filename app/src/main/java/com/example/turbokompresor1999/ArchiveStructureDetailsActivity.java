package com.example.turbokompresor1999;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Objects;

public class ArchiveStructureDetailsActivity extends AppCompatActivity {
    File data;
    File dataCopy;
    int requestCode;

    TextView tvFilename;
    TextView tvPath;
    TextView tvCompressedSize;
    TextView tvOriginalSize;
    TextView tvCompressionRatio;
    TextView tvAppliedAlgorithms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive_structure_details);
        Bundle extras = getIntent().getExtras();
        requestCode = extras.getInt("requestCode");

        tvFilename = findViewById(R.id.tv_filename);
        tvPath = findViewById(R.id.tv_path);
        tvCompressedSize = findViewById(R.id.tv_compressed_size);
        tvOriginalSize = findViewById(R.id.tv_original_size);
        tvCompressionRatio = findViewById(R.id.tv_compression_ratio);
        tvAppliedAlgorithms = findViewById(R.id.tv_applied_algorithms);

        if (requestCode == Codes.Request.details) {
            long id = extras.getLong("lookup_id");

            data = (File) ArchiveManager.getInstance().getStructureFromCurrentContent(id);

            dataCopy = new File();
            dataCopy.setName(data.name);
            dataCopy.setLookupId(data.lookup_id);
            dataCopy.setPath(data.path);

        }/*else if (requestCode == Codes.Request.newCrime) {
            data = CrimeLab.get(this).appendCrime(new Crime());
            data.setId(UUID.randomUUID());
            data.setDate(new Date(Calendar.getInstance().getTimeInMillis()));
            data.setSolved(false);
        }*/
        updateViews();
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = getIntent();
        returnIntent.putExtra("lookup_id", data.lookup_id);

        updateCrime();

        if (requestCode == Codes.Request.details) {

            if (Objects.equals(data.name, dataCopy.name) &&
                Objects.equals(data.lookup_id, dataCopy.lookup_id) &&
                Objects.equals(data.path, dataCopy.path))
            {
                setResult(Codes.Result.noActionRequired, returnIntent);
            } else {
                setResult(Codes.Result.crimeModification, returnIntent);
            }
        } else if (requestCode == Codes.Request.newCrime) {
            setResult(Codes.Result.crimeAdded, returnIntent);
        }
        finish();
    }

    private void updateViews() {
        tvFilename.setText("File name: " + data.name);
        tvPath.setText("Path: " + data.path);
        tvCompressedSize.setText("Compressed size: " + scaleBytesToBiggerUnit(data.compressed_size));
        tvOriginalSize.setText("Original size: " + scaleBytesToBiggerUnit(data.original_size));
        tvCompressionRatio.setText("Compression ratio: " + (float) data.compressed_size / data.original_size * 100 + "% (lower is better)");
        tvAppliedAlgorithms.setText(getPipelineStringFromFlags(data.flags));
    }

    private String getPipelineStringFromFlags(BitSet flags)
    {
        String blockSizeStr = "File was compressed in blocks of up to " + getScaledBlockSizeFromFlags(flags) + "\n\n"
                + "Applied algorithms:\n";

        ArrayList<String> algorithms = new ArrayList<>();
        if (flags.get(File.Flags.BWT)) algorithms.add("Burrows-Wheeler transform\n");
        if (flags.get(File.Flags.MTF)) algorithms.add("Move-To-Front\n");
        if (flags.get(File.Flags.RLE)) algorithms.add("Run-length encoding\n");
        if (flags.get(File.Flags.AC_order0)) algorithms.add("Arithmetic coding (simple model)\n");
        if (flags.get(File.Flags.AC_order1)) algorithms.add("Arithmetic coding (more complex model)\n");
        if (flags.get(File.Flags.rANS)) algorithms.add("Asymmetric Numeral Systems (simple model)\n");

        String checksumUsed = "";
        if (flags.get(File.Flags.SHA_256)) checksumUsed = "SHA-256\n";
        if (flags.get(File.Flags.CRC32)) checksumUsed = "CRC-32\n";
        if (flags.get(File.Flags.SHA_1)) checksumUsed = "SHA-1\n";

        if (checksumUsed.isEmpty()) {
            checksumUsed = "\n\nNo checksum was used\n";
        } else {
            checksumUsed = "\n\nChecksum algorithm: " + checksumUsed;
        }

        return blockSizeStr + String.join("↓\n", algorithms) + checksumUsed;
    }

    public static String getScaledBlockSizeFromFlags(BitSet flags) {
        int block_size = 1 << 24;
        if(flags.get(File.Flags.BlockSizeDiv2)) block_size >>= 1;
        if(flags.get(File.Flags.BlockSizeDiv4)) block_size >>= 2;
        if(flags.get(File.Flags.BlockSizeDiv16)) block_size >>= 4;
        if(flags.get(File.Flags.BlockSizeDiv256)) block_size >>= 8;

        return scaleBytesToBiggerUnit(block_size);
    }

    public static int getBlockSizeFromFlags(BitSet flags) {
        int block_size = 1 << 24;
        if(flags.get(File.Flags.BlockSizeDiv2)) block_size >>= 1;
        if(flags.get(File.Flags.BlockSizeDiv4)) block_size >>= 2;
        if(flags.get(File.Flags.BlockSizeDiv16)) block_size >>= 4;
        if(flags.get(File.Flags.BlockSizeDiv256)) block_size >>= 8;

        return block_size;
    }

    public static String scaleBytesToBiggerUnit(long bytes) {
        String unit = "B";
        float bytes_f = bytes;

        if (bytes_f > 1024) {
            bytes_f /= 1024;
            unit = "KB";
        }

        if (bytes_f > 1024) {
            bytes_f /= 1024;
            unit = "MB";
        }

        if (bytes_f > 1024) {
            bytes_f /= 1024;
            unit = "GB";
        }

        if (!unit.equals("B"))
            return String.format("%.1f", bytes_f) + " " + unit;
        else
            return "" + bytes + " " + unit;
    }

    private void updateCrime() {
        /*
        data.setTitle(etTitle.getText().toString());
        data.setDate(new Date(yearMonthDay.getTime() + time));
        data.setSolved(cbSolved.isChecked());*/
    }

    public void deleteCurrentCrime(View v) {
        throw new RuntimeException("probowano skasować. nie zaimplementowane");
        /*
        Intent returnIntent = getIntent();
        int index = ArchiveManager.getInstance().deleteCrime(data.getId());
        returnIntent.putExtra("crimeIndex", index);

        if (requestCode == Codes.Request.newCrime) {
            setResult(Codes.Result.noActionRequired, returnIntent);
        }
        else {
            setResult(Codes.Result.crimeDeletion, returnIntent);
        }
        finish();*/
    }
}