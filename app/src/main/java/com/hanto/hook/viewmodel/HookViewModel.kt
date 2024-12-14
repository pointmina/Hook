package com.hanto.hook.viewmodel

import HookRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanto.hook.data.Hook
import com.hanto.hook.data.HookWithTags
import kotlinx.coroutines.launch

class HookViewModel(private val hookRepository: HookRepository) : ViewModel() {

    // LiveData for Hooks
    private val _hooks = MutableLiveData<List<Hook>>()
    val hooks: LiveData<List<Hook>> get() = _hooks

    // LiveData for Hooks with Tags
    private val _hooksWithTags = MutableLiveData<List<HookWithTags>>()
    val hooksWithTags: LiveData<List<HookWithTags>> get() = _hooksWithTags

    // LiveData for Tags
    private val _tags = MutableLiveData<List<String>>()
    val tags: LiveData<List<String>> get() = _tags

    // LiveData for error messages
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    /**
     * Insert a new hook with selected tags
     */
    fun insertHook(hook: Hook, selectedTags: List<String>) {
        viewModelScope.launch {
            try {
                hookRepository.insertHook(hook, selectedTags)
                loadAllHooksWithTags() // Refresh the data after insertion
            } catch (e: Exception) {
                _errorMessage.value = "Error inserting hook: ${e.message}"
            }
        }
    }

    /**
     * Update an existing hook
     */
    fun updateHook(hook: Hook, updatedTags: List<String>) {
        viewModelScope.launch {
            try {
                hookRepository.updateHook(hook, updatedTags)
                loadAllHooksWithTags() // Refresh the data after update
            } catch (e: Exception) {
                _errorMessage.value = "Error updating hook: ${e.message}"
            }
        }
    }

    /**
     * Delete a hook by ID
     */
    fun deleteHook(hookId: Long) {
        viewModelScope.launch {
            try {
                hookRepository.deleteHook(hookId)
                loadAllHooksWithTags() // Refresh the data after deletion
            } catch (e: Exception) {
                _errorMessage.value = "Error deleting hook: ${e.message}"
            }
        }
    }

    /**
     * Load all hooks with their associated tags
     */
    fun loadAllHooksWithTags() {
        viewModelScope.launch {
            try {
                val hooksWithTags = hookRepository.getAllHooksWithTags()
                _hooksWithTags.value = hooksWithTags
            } catch (e: Exception) {
                _errorMessage.value = "Error loading hooks with tags: ${e.message}"
            }
        }
    }

    /**
     * Get hooks filtered by a specific tag
     */
    fun loadHooksByTag(tagName: String) {
        viewModelScope.launch {
            try {
                val filteredHooks = hookRepository.getHooksByTag(tagName)
                _hooks.value = filteredHooks
            } catch (e: Exception) {
                _errorMessage.value = "Error loading hooks by tag: ${e.message}"
            }
        }
    }

    /**
     * Load all tags
     */
    fun loadAllTags() {
        viewModelScope.launch {
            try {
                val tagList = hookRepository.getAllTagsName()
                _tags.value = tagList
            } catch (e: Exception) {
                _errorMessage.value = "Error loading tags: ${e.message}"
            }
        }
    }

    /**
     * Clear error messages
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
