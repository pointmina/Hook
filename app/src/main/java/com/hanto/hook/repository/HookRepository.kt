import com.hanto.hook.dao.HookDao
import com.hanto.hook.data.Hook
import com.hanto.hook.data.HookTagMapping
import com.hanto.hook.data.HookWithTags
import com.hanto.hook.data.Tag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HookRepository(private val hookDao: HookDao) {

    // 훅 삽입
    suspend fun insertHook(hook: Hook, selectedTags: List<String>) {
        withContext(Dispatchers.IO) {
            val hookId = hookDao.insertHook(hook)

            if(selectedTags.isNotEmpty()){
                selectedTags.distinct().forEach { tagName ->
                    var tag = hookDao.getTagByName(tagName)

                    if (tag == null) {
                        tag = Tag(name = tagName)
                        hookDao.insertTag(tag)
                    }

                    hookDao.run { insertMapping(HookTagMapping(hookId = hookId, tagId = tag.id)) }
                }
            }

        }
    }

    // 훅 업데이트
    suspend fun updateHook(hook: Hook, updatedTags: List<String>) {
        withContext(Dispatchers.IO) {
            hookDao.updateHook(hook)

            // 기존 태그-훅 매핑 삭제
            hookDao.deleteMappingsByHookId(hook.id)

            // 새로운 태그 추가 및 매핑 생성
            updatedTags.distinct().forEach { tagName ->
                var tag = hookDao.getTagByName(tagName)

                if (tag == null) {
                    tag = Tag(name = tagName)
                    hookDao.insertTag(tag)
                }

                hookDao.run { insertMapping(HookTagMapping(hookId = hook.id, tagId = tag.id)) }
            }
        }
    }

    // 훅 삭제
    suspend fun deleteHook(hookId: Long) {
        withContext(Dispatchers.IO) {
            hookDao.deleteMappingsByHookId(hookId) // 태그 매핑 삭제
            hookDao.deleteHookById(hookId) // 훅 삭제
        }
    }

    // 태그 가져오기
    suspend fun getAllTagsName(): List<String> {
        return withContext(Dispatchers.IO) {
            hookDao.getAllTagNames()
        }
    }

    // 태그별로 훅 가져오기
    suspend fun getHooksByTag(tagName: String): List<Hook> {
        return withContext(Dispatchers.IO) {
            hookDao.getHooksByTag(tagName)
        }
    }

    suspend fun getAllHooksWithTags(): List<HookWithTags> {
        val hooks = hookDao.getAllHooks()
        val hookWithTagsList = mutableListOf<HookWithTags>()

        for (hook in hooks) {
            val tags = hookDao.getTagsForHook(hook.id)
            hookWithTagsList.add(HookWithTags(hook, tags))
        }

        return hookWithTagsList
    }
}