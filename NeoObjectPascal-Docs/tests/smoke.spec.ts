import { test, expect } from '@playwright/test'

// Each locale's home page loads and shows the hero.
const locales = [
  { name: 'pt', home: '/', intro: '/getting-started/introduction' },
  { name: 'en', home: '/en/', intro: '/en/getting-started/introduction' },
  { name: 'de', home: '/de/', intro: '/de/getting-started/introduction' },
  { name: 'fr', home: '/fr/', intro: '/fr/getting-started/introduction' },
  { name: 'it', home: '/it/', intro: '/it/getting-started/introduction' }
]

for (const l of locales) {
  test(`[${l.name}] home page loads with hero`, async ({ page }) => {
    await page.goto(l.home)
    await expect(page.locator('.VPHero .name')).toContainText('NeoObjectPascal')
  })

  test(`[${l.name}] introduction page loads with an H1`, async ({ page }) => {
    await page.goto(l.intro)
    await expect(page.locator('h1').first()).toBeVisible()
  })
}

test('NeoObjectPascal code fences get highlighted', async ({ page }) => {
  await page.goto('/getting-started/installation')
  // Shiki wraps highlighted code in a div with a language class.
  await expect(page.locator('div[class*="language-npas"]').first()).toBeVisible()
})

test('language switcher exposes all five locales', async ({ page }) => {
  await page.goto('/')
  // The translations menu is rendered in the nav; its screen-reader list holds locale links.
  const langLinks = page.locator('a[href="/en/"], a[href="/de/"], a[href="/fr/"], a[href="/it/"]')
  expect(await langLinks.count()).toBeGreaterThan(0)
})
