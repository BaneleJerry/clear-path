package com.banelethabede.clear_path_parent.admin;

public record DashboardStats(
        long totalUsers,
        long totalOrganisations,
        long pendingInvites,
        long activeToday
) {}