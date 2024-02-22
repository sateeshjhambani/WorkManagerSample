# WorkManagerSample

The app provides a sample usage of Jetpack's WorkManager API to chain a couple of tasks (workers in WorkManager terms) and use the output response on the UI.

The first is a DownloadWorker that downloads an image from [Unsplash](https://images.unsplash.com), saves it to the app cache, and returns the image URI. This worker is then chained to the second worker called ColorFilterWorker, which takes the original image, applies a colour filter, and returns the filtered image URI.

![WorkManagerSample](https://github.com/sateeshjhambani/WorkManagerSample/assets/60574717/a48e43a0-7ac9-4b1b-8b99-8db5d900f756)

## Usage

The DownloadWorker worker calls an API to download an image, stores it in the app's cache directory, and returns the image URI.

```kotlin
class DownloadWorker(
    workerParams: WorkerParameters,
    private val context: Context
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val response = FileApi.instance.downloadImage()
        response.body()?.let { body ->
            return withContext(Dispatchers.IO) {
                val file = File(context.cacheDir, "image.jpg")
                val outputStream = FileOutputStream(file)
                outputStream.use { stream ->
                    try {
                        stream.write(body.bytes())
                    } catch (e: IOException) {
                        return@withContext Result.failure(
                            workDataOf(
                                WorkerKeys.ERROR_MSG to e.localizedMessage
                            )
                        )
                    }
                }
                Result.success(
                    workDataOf(
                        WorkerKeys.IMAGE_URI to file.toUri().toString()
                    )
                )
            }
        }

        // return error with Result.failure(
        //    workDataOf(
        //        WorkerKeys.ERROR_MSG to "Some Error"
        //    )
        // )
    }
```

The ColorFilterWorker worker takes the downloaded image URI as input data, applies a color filter, save it to the app's cache directory and returns the filtered image URI.

```kotlin
class ColorFilterWorker(
    private val context: Context,
    private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val imageFile = workerParams.inputData.getString(WorkerKeys.IMAGE_URI)
            ?.toUri()
            ?.toFile()

        return imageFile?.let { file ->
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            val resultBitmap = bitmap.copy(bitmap.config, true)
            val paint = Paint()
            paint.colorFilter = LightingColorFilter(0x08FF04, 1)
            val canvas = Canvas(resultBitmap)
            canvas.drawBitmap(resultBitmap, 0f, 0f, paint)

            withContext(Dispatchers.IO) {
                val resultImageFile = File(context.cacheDir, "new_image.jpg")
                val outputStream = FileOutputStream(resultImageFile)
                val successful = resultBitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    90,
                    outputStream
                )

                if (successful) {
                    Result.success(
                        workDataOf(
                            WorkerKeys.FILTER_URI to resultImageFile.toUri().toString()
                        )
                    )
                } else {
                    Result.failure()
                }
            }
        } ?: Result.failure()
    }
}
```

Instantiating the workers (with some constraints) and the WorkManager itself.

```kotlin
val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
    .setConstraints(
        Constraints.Builder()
            .setRequiredNetworkType(
                NetworkType.CONNECTED
            )
            .build()
    )
    .build()

val colorFilterRequest = OneTimeWorkRequestBuilder<ColorFilterWorker>()
    .build()

val workManager = WorkManager.getInstance(applicationContext)
```

Chaining worker execution, one after another

```kotlin
workManager
    .beginUniqueWork(
        "download_image",
        ExistingWorkPolicy.KEEP,
        downloadRequest
    )
    .then(colorFilterRequest)
    .enqueue()
```

## References

[Docs](https://developer.android.com/develop/background-work/background-tasks/persistent/getting-started)
