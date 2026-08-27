import { expect, test } from '@playwright/test';
import type { Page } from '@playwright/test';

const login = async (page: Page, username = 'james') => {
  await page.goto('/');
  await page.getByLabel('帳號').fill(username);
  await page.getByLabel('密碼').fill('password123');
  await page.getByRole('button', { name: '登入' }).click();
  await expect(page.getByText('個人支出分析')).toBeVisible();
};

test.describe('Finx 使用者流程', () => {
  test('登入後可以查看 Dashboard 圖表', async ({ page }) => {
    await login(page);
    await expect(page.getByText('最近 6 個月支出')).toBeVisible();
    await expect(page.getByText('支出占比')).toBeVisible();
    await expect(page.getByText('分類支出排行')).toBeVisible();
    await expect(page.getByText('信用卡支出比較')).toBeVisible();
    await expect(page.locator('.recharts-wrapper')).toHaveCount(4);
  });

  test('切換月份會更新 Dashboard，點擊分類會聯動支出篩選', async ({ page }) => {
    await login(page);
    const monthSelect = page.getByLabel('分析月份');
    await monthSelect.selectOption('2026-07');
    await expect(page.getByText('2026 年 07 月儀表板')).toBeVisible();
    await page.locator('.category-chart .recharts-bar-rectangle').first().click();
    await expect(page.getByRole('heading', { name: '支出紀錄' })).toBeVisible();
    await expect(page.getByText('清除篩選')).toBeVisible();
  });

  test('使用者可以新增並刪除支出', async ({ page }) => {
    await login(page);
    await page.getByRole('button', { name: '支出紀錄' }).click();
    const merchant = `E2E 測試商店 ${Date.now()}`;
    await page.getByLabel('商店').fill(merchant);
    await page.getByLabel('金額').fill('123');
    await page.getByRole('button', { name: '儲存' }).click();
    await expect(page.getByText('支出已新增')).toBeVisible();
    await expect(page.getByText(merchant)).toBeVisible();
    page.once('dialog', (dialog) => dialog.accept());
    await page.getByText(merchant).locator('..').getByRole('button', { name: '刪除' }).click();
    await expect(page.getByText('支出已刪除')).toBeVisible();
  });

  test('使用者可以查看信用卡與帳號設定', async ({ page }) => {
    await login(page);
    await page.getByRole('button', { name: '信用卡' }).click();
    await expect(page.getByText('玫瑰Giving卡')).toBeVisible();
    await expect(page.getByText('CUBE卡')).toBeVisible();
    await page.getByRole('button', { name: '設定' }).click();
    await expect(page.getByRole('heading', { name: '個人資料' })).toBeVisible();
    await expect(page.getByRole('heading', { name: '登入安全' })).toBeVisible();
  });

  test('一般使用者看不到 Admin 導覽', async ({ page }) => {
    await login(page);
    await expect(page.getByRole('button', { name: 'Admin' })).toHaveCount(0);
  });
});
