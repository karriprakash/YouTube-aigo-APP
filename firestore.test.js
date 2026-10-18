const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// --- SECURITY BOUNDS TESTS ---

test("Unauthenticated user: cannot read user projects", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("projects").get());
});

test("Authenticated user: cannot read another user's projects", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).collection("projects").doc("proj1").set({
      userId: BOB_UID,
      title: "Bob's Telugu Video",
      videoUri: "asset://sample.mp4",
      originalDurationMs: 30000,
      trimStartMs: 0,
      trimEndMs: 30000,
      aspectRatio: "9:16",
      createdAt: new Date(),
      updatedAt: new Date()
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).collection("projects").doc("proj1").get());
});

test("Authenticated user: can create and read their own project", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const aliceProjRef = aliceDb.collection("users").doc(ALICE_UID).collection("projects").doc("proj_alice_1");

  await assertSucceeds(
    aliceProjRef.set({
      userId: ALICE_UID,
      title: "Alice Telugu Reel",
      videoUri: "asset://video.mp4",
      originalDurationMs: 25000,
      trimStartMs: 2000,
      trimEndMs: 20000,
      aspectRatio: "9:16",
      createdAt: new Date(),
      updatedAt: new Date()
    })
  );

  await assertSucceeds(aliceProjRef.get());
});

test("Authenticated user: cannot spoof userId in project creation", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const badProjRef = aliceDb.collection("users").doc(ALICE_UID).collection("projects").doc("proj_bad");

  await assertFails(
    badProjRef.set({
      userId: BOB_UID, // Spoofed!
      title: "Malicious Project",
      videoUri: "asset://video.mp4",
      originalDurationMs: 25000,
      trimStartMs: 0,
      trimEndMs: 25000,
      aspectRatio: "9:16",
      createdAt: new Date(),
      updatedAt: new Date()
    })
  );
});

test("Authenticated user: can create and read their own media asset", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const assetRef = aliceDb.collection("users").doc(ALICE_UID).collection("assets").doc("asset_1");

  await assertSucceeds(
    assetRef.set({
      userId: ALICE_UID,
      name: "telugu_raw.mp4",
      type: "VIDEO",
      uri: "asset://telugu_raw.mp4",
      durationMs: 15000,
      language: "te",
      createdAt: new Date(),
      updatedAt: new Date()
    })
  );

  await assertSucceeds(assetRef.get());
});
