//DB <-> ViewModel
import com.hanto.hook.data.model.Hook
import com.hanto.hook.data.model.HookTagMapping
import com.hanto.hook.data.model.Tag
import com.hanto.hook.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HookRepository (private val appDatabase: AppDatabase) {

    // 싱글톤 인스턴스 관리
    companion object {
        @Volatile
        private var instance: HookRepository? = null

        fun getInstance(appDatabase: AppDatabase): HookRepository {
            return instance ?: synchronized(this) {
                instance ?: HookRepository(appDatabase).also { instance = it }
            }
        }
    }

    suspend fun insertHook(hook: Hook) {
        appDatabase.hookDao().insertHook(hook)
    }

    suspend fun insertTag(tag: Tag) {
        appDatabase.hookDao().insertTag(tag)
    }


    fun insertMapping(hookTag: HookTagMapping) {
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.hookDao().insertMapping(hookTag)
        }
    }

    fun deleteHook(hookId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.hookDao().deleteHookById(hookId)
        }
    }

    fun deleteTagByHookId(hookId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.hookDao().deleteTagByHookId(hookId)
        }
    }

    fun deleteMappingsByHookId(hookId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.hookDao().deleteMappingsByHookId(hookId)
        }
    }

    fun deleteHookAndTags(hookId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.hookDao().deleteHookAndTags(hookId)
        }
    }

    fun getAllHooks() {
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.hookDao().getAllHooks()
        }
    }

    fun getTagsForHook(hookId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.hookDao().getTagsForHook(hookId)
        }
    }

    fun getAllTagNames() {
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.hookDao().getAllTagNames()
        }
    }

    fun getHookByTag(hookId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.hookDao().getHookByTag(hookId)
        }
    }
}
