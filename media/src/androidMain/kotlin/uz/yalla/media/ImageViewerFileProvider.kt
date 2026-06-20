package uz.yalla.media

import androidx.core.content.FileProvider
import uz.yalla.sdk.media.R

// TODO(quality, needs-decision): finding #24 — this is runtime plumbing (only referenced by the
// AndroidManifest provider by FQN, no Kotlin caller), so it should not be in the consumer `.api`.
// Marking it `internal` keeps it classloader-resolvable (the class stays public bytecode) and drops
// it from the dump, but that interplay (manifest FQN resolution + BCV ignore) needs a real build to
// confirm; leaving it `public` to avoid an unverified manifest-resolution break.
public class ImageViewerFileProvider : FileProvider(R.xml.file_paths)
