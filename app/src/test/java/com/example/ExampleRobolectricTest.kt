package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.TeachingMode
import com.example.domain.CurriculumData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Vidya AI", appName)
    }

    @Test
    fun `curriculum data contains all standard grades and intermediate streams`() {
        assertTrue(CurriculumData.AVAILABLE_GRADES.contains("Class 10"))
        assertTrue(CurriculumData.AVAILABLE_GRADES.contains("Intermediate 1st Year"))
        assertTrue(CurriculumData.AVAILABLE_GRADES.contains("Intermediate 2nd Year"))

        val class10Subjects = CurriculumData.getSubjectsFor("Class 10")
        assertTrue(class10Subjects.isNotEmpty())
        assertTrue(class10Subjects.any { it.name.contains("Math") })
        assertTrue(class10Subjects.any { it.name.contains("Physics") })

        val interMpcSubjects = CurriculumData.getSubjectsFor("Intermediate 1st Year", "MPC")
        assertTrue(interMpcSubjects.any { it.name.contains("Math") })
        assertTrue(interMpcSubjects.any { it.name.contains("Physics") })
        assertTrue(interMpcSubjects.any { it.name.contains("Chemistry") })

        val interBipcSubjects = CurriculumData.getSubjectsFor("Intermediate 1st Year", "BiPC")
        assertTrue(interBipcSubjects.any { it.name.contains("Biology") })
    }

    @Test
    fun `all grades from Class 1 to 12 have relevant curriculum subjects`() {
        val testGrades = listOf(
            "Class 1", "Class 3", "Class 6", "Class 9", "Class 10",
            "Intermediate 1st Year", "Intermediate 2nd Year"
        )
        for (grade in testGrades) {
            val subjects = CurriculumData.getSubjectsFor(grade, "MPC")
            assertTrue("Grade $grade should have subjects defined", subjects.isNotEmpty())
            for (subject in subjects) {
                assertTrue("Subject should have valid name", subject.name.isNotBlank())
                assertTrue("Subject should have chapters", subject.chapters.isNotEmpty())
            }
        }
    }

    @Test
    fun `ai provider abstraction returns valid curriculum responses offline`() = kotlinx.coroutines.runBlocking {
        val provider = com.example.data.ai.LocalEducationalProvider()
        val request = com.example.data.ai.AIRequest(
            prompt = "Explain Quadratic Formula",
            grade = "Class 10",
            subject = "Mathematics",
            chapter = "Quadratic Equations"
        )
        val result = provider.generateResponse(request)
        assertTrue(result.isSuccess)
        val response = result.getOrThrow()
        assertTrue(response.text.contains("Quadratic", ignoreCase = true))
        assertEquals(com.example.data.ai.AIProviderType.LOCAL_OFFLINE, response.providerUsed)
    }
}
