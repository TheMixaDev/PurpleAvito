package org.bigbrainmm.avitoadminapi.entity;

/**
 * Роли для авторизации через JWT, но... сказали она не нужна
 * Они просто есть
 */
public enum Role {
    /**
     * Роль пользователя
     */
    ROLE_USER,
    /**
     * Роль администратора
     */
    ROLE_ADMIN
}