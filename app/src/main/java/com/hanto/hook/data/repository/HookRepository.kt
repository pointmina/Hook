import androidx.lifecycle.LiveData
import com.hanto.hook.data.model.Hook
import com.hanto.hook.data.model.Tag
import com.hanto.hook.database.AppDatabase

class HookRepository(private val appDatabase: AppDatabase) {

    companion object {
        @Volatile
        private var instance: HookRepository? = null

        fun getInstance(appDatabase: AppDatabase): HookRepository {
            return instance ?: synchronized(this) {
                instance ?: HookRepository(appDatabase).also { instance = it }
            }
        }
    }

    // 데이터 삽입 메서드
    fun insertHook(hook: Hook) {
        appDatabase.hookDao().insertHook(hook) // CoroutineScope 제거
    }

    fun insertTag(tag: Tag) {
        appDatabase.hookDao().insertTag(tag) // CoroutineScope 제거
    }

    // 데이터 삭제 메서드
    fun deleteHook(hookId: String) {
        appDatabase.hookDao().deleteHookById(hookId) // CoroutineScope 제거
    }

    fun deleteTagByHookId(hookId: String) {
        appDatabase.hookDao().deleteTagByHookId(hookId) // CoroutineScope 제거
    }


    fun deleteHookAndTags(hookId: String) {
        appDatabase.hookDao().deleteHookAndTags(hookId)
    }

    //데이터 업데이트 메서드
    fun updateHook(hook: Hook) {
        appDatabase.hookDao().updateHook(hook)
    }

    fun updateTagsForHook(hookId: String, selectedTags: List<String>) {
        appDatabase.hookDao().deleteTagByHookId(hookId)

        selectedTags.forEach { tagName ->
            val tag = Tag(hookId = hookId, name = tagName)
            appDatabase.hookDao().insertTag(tag)
        }
    }

    suspend fun updateHookAndTags(hook: Hook, selectedTags: List<String>) {
        appDatabase.hookDao().updateHook(hook)
        appDatabase.hookDao().deleteTagByHookId(hook.hookId)

        selectedTags.forEach { tagName ->
            val tag = Tag(hookId = hook.hookId, name = tagName)
            appDatabase.hookDao().insertTag(tag)
        }
    }


    // 데이터 조회 메서드 (LiveData 반환)
    fun getAllHooks(): LiveData<List<Hook>> {
        return appDatabase.hookDao().getAllHooks()
    }

    fun getTagsForHook(hookId: String): LiveData<List<Tag>>? {
        return appDatabase.hookDao().getTagsForHook(hookId)
    }

    fun getAllTagNames(): LiveData<List<String>> {
        return appDatabase.hookDao().getAllTagNames()
    }

    fun getHookByTagName(tagName: String): LiveData<List<Hook>?> {
        return appDatabase.hookDao().getHooksByTagName(tagName)
    }
}
