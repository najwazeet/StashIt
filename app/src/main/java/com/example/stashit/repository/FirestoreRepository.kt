package com.example.stashit.repository

import com.example.stashit.data.Acara
import com.example.stashit.data.RincianBiaya
import com.example.stashit.data.SavingsHistoryEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.stashit.data.AcaraWithTotal

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    // ===== ACARA =====

    suspend fun addAcara(acara: Acara): String {
        val docRef = db.collection("acara").add(acara).await()
        return docRef.id
    }

    suspend fun getAcaraList(): List<Acara> {
        val snapshot = db.collection("acara")
            .whereEqualTo("id_user", currentUserId)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Acara::class.java)?.apply { idAcara = doc.id }
        }
    }

    suspend fun getAcaraWithTotals(): List<AcaraWithTotal> {
        val acaraList = getAcaraList()
        return acaraList.map { acara ->
            val rincianList = getRincianBiayaList(acara.idAcara)
            val totalTarget = rincianList.sumOf { it.target_nominal }.toLong()
            val totalTerkumpul = rincianList.sumOf { it.nominal_terkumpul }.toLong()
            AcaraWithTotal(acara, totalTarget, totalTerkumpul)
        }
    }

    suspend fun getAcaraById(idAcara: String): Acara? {
        val doc = db.collection("acara").document(idAcara).get().await()
        return doc.toObject(Acara::class.java)?.apply { this.idAcara = doc.id }
    }

    suspend fun deleteAcara(idAcara: String) {
        // Hapus semua rincian biaya terkait dulu
        val rincianSnapshot = db.collection("rincian_biaya")
            .whereEqualTo("id_acara", idAcara)
            .get()
            .await()

        for (doc in rincianSnapshot.documents) {
            db.collection("rincian_biaya").document(doc.id).delete().await()
        }

        db.collection("acara").document(idAcara).delete().await()
    }

    suspend fun updateAcara(idAcara: String, namaAcara: String, tanggal: String, lokasi: String) {
        db.collection("acara")
            .document(idAcara)
            .update(
                mapOf(
                    "nama_acara" to namaAcara,
                    "tanggal" to tanggal,
                    "lokasi" to lokasi
                )
            )
            .await()
    }

    // ===== RINCIAN BIAYA =====

    suspend fun addRincianBiaya(rincianBiaya: RincianBiaya): String {
        val docRef = db.collection("rincian_biaya").add(rincianBiaya).await()
        return docRef.id
    }

    suspend fun getRincianBiayaById(idRincianBiaya: String): RincianBiaya? {
        val doc = db.collection("rincian_biaya").document(idRincianBiaya).get().await()
        return doc.toObject(RincianBiaya::class.java)?.apply { this.idRincianBiaya = doc.id }
    }

    suspend fun getRincianBiayaList(idAcara: String): List<RincianBiaya> {
        val snapshot = db.collection("rincian_biaya")
            .whereEqualTo("id_acara", idAcara)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(RincianBiaya::class.java)?.apply { idRincianBiaya = doc.id }
        }
    }

    suspend fun updateNominalTerkumpul(idRincianBiaya: String, nominalBaru: Double) {
        db.collection("rincian_biaya")
            .document(idRincianBiaya)
            .update("nominal_terkumpul", nominalBaru)
            .await()
    }

    suspend fun updateRincianBiaya(idRincianBiaya: String, namaKebutuhan: String, targetNominal: Double) {
        db.collection("rincian_biaya")
            .document(idRincianBiaya)
            .update(
                mapOf(
                    "nama_kebutuhan" to namaKebutuhan,
                    "target_nominal" to targetNominal
                )
            )
            .await()
    }

    suspend fun deleteRincianBiaya(idRincianBiaya: String) {
        // Hapus history subcollection dulu
        val historySnapshot = db.collection("rincian_biaya")
            .document(idRincianBiaya)
            .collection("history")
            .get()
            .await()

        for (doc in historySnapshot.documents) {
            db.collection("rincian_biaya")
                .document(idRincianBiaya)
                .collection("history")
                .document(doc.id)
                .delete()
                .await()
        }

        db.collection("rincian_biaya").document(idRincianBiaya).delete().await()
    }

    // ===== SAVINGS HISTORY (subcollection) =====

    suspend fun addHistoryEntry(idRincianBiaya: String, entry: SavingsHistoryEntry) {
        db.collection("rincian_biaya")
            .document(idRincianBiaya)
            .collection("history")
            .add(entry)
            .await()
    }

    suspend fun getHistoryList(idRincianBiaya: String): List<SavingsHistoryEntry> {
        val snapshot = db.collection("rincian_biaya")
            .document(idRincianBiaya)
            .collection("history")
            .orderBy("created_at", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(SavingsHistoryEntry::class.java)?.apply { idHistory = doc.id }
        }
    }
}