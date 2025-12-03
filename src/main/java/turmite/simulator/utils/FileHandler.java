package turmite.simulator.utils;

import turmite.simulator.models.Direction;
import turmite.simulator.models.Rule;
import turmite.simulator.ui.Dialogs;
import turmite.simulator.ui.RuleSelectorComboBox;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class FileHandler {
    private  FileHandler() {}

    public static String importRuleset(JFrame frame, String fileExt, String fileDir) throws IOException {
        FileDialog fd = new FileDialog(frame, "Import Rule", FileDialog.LOAD);
        fd.setDirectory("%UserProfile%\\Downloads");
        fd.setFile("*" + fileExt);
        fd.setVisible(true);

        String selectedFilePath = fd.getDirectory() + fd.getFile();
        if (selectedFilePath.equals("nullnull")) return null;
        if (fd.getFile().equals(RuleSelectorComboBox.NEW_RULESET_STR + fileExt)) {
            Dialogs.showErrorDialog(null, String.format("Ruleset name cannot be %s.", RuleSelectorComboBox.NEW_RULESET_STR));
            return null;
        }

        File selectedFile = new File(selectedFilePath);
        String copiedFilePath = String.format("%s\\%s", fileDir, selectedFile.getName());
        Files.copy(Path.of(selectedFilePath), Path.of(copiedFilePath), StandardCopyOption.REPLACE_EXISTING);

        return copiedFilePath;
    }

    public static void exportRuleset(JFrame frame, Ruleset ruleset, String fileDir, String fileExt) throws FileNotFoundException, IllegalArgumentException {
        FileDialog fd = new FileDialog(frame, "Export Ruleset", FileDialog.SAVE);
        fd.setDirectory(fileDir);
        fd.setFile("*" + fileExt);
        fd.setVisible(true);

        String rulesetFile = fd.getDirectory() + fd.getFile();
        if (rulesetFile.equals("nullnull")) return;
        if (fd.getFile().equals(RuleSelectorComboBox.NEW_RULESET_STR + fileExt)) throw new IllegalArgumentException(String.format("Ruleset name cannot be %s.",  RuleSelectorComboBox.NEW_RULESET_STR));

        OutputStream outputStream = new FileOutputStream(rulesetFile);
        JsonWriterFactory writerFactory = Json.createWriterFactory(Map.of(JsonGenerator.PRETTY_PRINTING, true));
        JsonWriter writer = writerFactory.createWriter(outputStream);
        JsonObjectBuilder rulesetObject = Json.createObjectBuilder();
        JsonArrayBuilder rulesetArray = Json.createArrayBuilder();

        for (Rule  rule : ruleset.getRules()) {
            JsonObject ruleObject = Json.createObjectBuilder()
                    .add("currState", rule.getCurrState())
                    .add("currColor", rule.getCurrColor())
                    .add("turnDir", String.valueOf(Direction.getCharFromTurnDir(rule.getTurnDir())))
                    .add("newColor", rule.getNewColor())
                    .add("newState", rule.getNewState())
                    .build();

            rulesetArray.add(ruleObject);
        }

        rulesetObject.add("ruleset", rulesetArray.build());
        writer.write(rulesetObject.build());
        writer.close();
    }
}
