package it.codex.notification.preferences;

import org.springframework.stereotype.Service;

@Service
public class PreferenceService {
    public boolean allowEmail(String userId) { return true; }
    public boolean allowSlack(String userId) { return true; }
    public boolean allowPush(String userId) { return true; }
}
