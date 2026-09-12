module.exports = async (driver) => {
  // Wait for the KMP engine/Compose to warm up
  await driver.wait(3000);

  // Example: Scroll down a list 5 times
  for (let i = 0; i < 5; i++) {
    await driver.swipe({ x: 500, y: 800 }, { x: 500, y: 200 });
    await driver.wait(1000); // Wait for momentum to settle
  }
};
