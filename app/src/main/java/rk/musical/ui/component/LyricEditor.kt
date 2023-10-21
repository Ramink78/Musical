package rk.musical.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import rk.musical.ui.theme.MusicalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LyricEditor(
    editorState: SheetState = rememberStandardBottomSheetState(),
    onSubmitClick: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by rememberSaveable {
        mutableStateOf("")
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = editorState
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp, max = 200.dp)

        )
        TextButton(
            onClick = { onSubmitClick(text) },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "Submit")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun LyricEditorDarkPreview() {
    MusicalTheme(darkTheme = true) {
        LyricEditor(onSubmitClick = {}, onDismiss = {})
    }
}
