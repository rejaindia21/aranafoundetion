package com.arnafoundation.app

import com.arnafoundation.app.data.model.UserRole
import com.arnafoundation.app.data.remote.dto.UserDto
import com.arnafoundation.app.data.remote.dto.normalizeBackendRole
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun normalizeBackendRole_adminRoutesToAdmin() {
    assertEquals(UserRole.ADMIN, normalizeBackendRole("Admin"))
    assertEquals(UserRole.ADMIN, normalizeBackendRole("ADMIN"))
    assertEquals(UserRole.ADMIN, normalizeBackendRole("Administrator"))
  }

  @Test
  fun normalizeBackendRole_mdAndCeoRouteToEmployee() {
    assertEquals(UserRole.EMPLOYEE, normalizeBackendRole("M.D"))
    assertEquals(UserRole.EMPLOYEE, normalizeBackendRole("C.E.O"))
    assertEquals(UserRole.EMPLOYEE, normalizeBackendRole("MD"))
    assertEquals(UserRole.EMPLOYEE, normalizeBackendRole("CEO"))
    assertEquals(UserRole.EMPLOYEE, normalizeBackendRole("Employee"))
    assertEquals(UserRole.EMPLOYEE, normalizeBackendRole("Staff"))
  }

  @Test
  fun normalizeBackendRole_customerRoutesToCustomer() {
    assertEquals(UserRole.CUSTOMER, normalizeBackendRole("Customer"))
    assertEquals(UserRole.CUSTOMER, normalizeBackendRole("Borrower"))
    assertEquals(UserRole.CUSTOMER, normalizeBackendRole("Member"))
  }

  @Test
  fun userDto_stetusBlockedCheck() {
    val unblockUser = UserDto(id = "1", role = "Admin", stetus = "Unblock")
    assertFalse(unblockUser.isBlocked)

    val blockedUser = UserDto(id = "3", role = "M.D", stetus = "Block")
    assertTrue(blockedUser.isBlocked)

    val inactiveUser = UserDto(id = "4", role = "C.E.O", status = "inactive")
    assertTrue(inactiveUser.isBlocked)
  }

  @Test
  fun userDto_adminRoleAndDesignationMapping() {
    val adminUser = UserDto(
      id = "1",
      username = "admin",
      employeeId = "E00000",
      role = "admin",
      originalRole = "Admin",
      designation = "Administrator",
      stetus = "Unblock"
    )
    assertEquals("E00000", adminUser.resolvedId)
    assertEquals("Admin", adminUser.resolvedRole)
    assertEquals("Administrator", adminUser.resolvedDesignation)
    assertEquals(UserRole.ADMIN, normalizeBackendRole(adminUser.resolvedRole))
    assertFalse(adminUser.isBlocked)
  }

  @Test
  fun userDto_ceoEmployeeRoleMapping() {
    val ceoUser = UserDto(
      id = "4",
      username = "Amit",
      employeeId = "E00001",
      role = "employee",
      originalRole = "",
      designation = "C.E.O",
      stetus = "Unblock"
    )
    assertEquals("E00001", ceoUser.resolvedId)
    assertEquals("employee", ceoUser.resolvedRole)
    assertEquals("C.E.O", ceoUser.resolvedDesignation)
    assertEquals(UserRole.EMPLOYEE, normalizeBackendRole(ceoUser.resolvedRole))
    assertFalse(ceoUser.isBlocked)
  }

  @Test
  fun userDto_mdEmployeeRoleMapping() {
    val mdUser = UserDto(
      id = "3",
      username = "Kakali",
      employeeId = "E00002",
      role = "employee",
      originalRole = "",
      designation = "M.D",
      stetus = "Unblock"
    )
    assertEquals("E00002", mdUser.resolvedId)
    assertEquals("employee", mdUser.resolvedRole)
    assertEquals("M.D", mdUser.resolvedDesignation)
    assertEquals(UserRole.EMPLOYEE, normalizeBackendRole(mdUser.resolvedRole))
    assertFalse(mdUser.isBlocked)
  }

  @Test
  fun userDto_customerRoleMapping() {
    val customerUser = UserDto(
      id = "M00000001",
      role = "customer",
      status = "Active"
    )
    assertEquals("M00000001", customerUser.resolvedId)
    assertEquals(UserRole.CUSTOMER, normalizeBackendRole(customerUser.resolvedRole))
    assertFalse(customerUser.isBlocked)
  }

  @Test
  fun retrofitUrlResolution_matchesArnaServerEndpoints() {
    val baseHttpUrl = "https://arnafoundation.com/api/app/".toHttpUrl()

    val userLoginUrl = baseHttpUrl.resolve("../api/api-login.php")?.toString()
    assertEquals("https://arnafoundation.com/api/api/api-login.php", userLoginUrl)

    val customerLoginUrl = baseHttpUrl.resolve("../api/api-customer-login.php")?.toString()
    assertEquals("https://arnafoundation.com/api/api/api-customer-login.php", customerLoginUrl)

    val adminCustomersUrl = baseHttpUrl.resolve("admin/admin-customers.php")?.toString()
    assertEquals("https://arnafoundation.com/api/app/admin/admin-customers.php", adminCustomersUrl)

    val adminLoansUrl = baseHttpUrl.resolve("admin/admin-loans.php")?.toString()
    assertEquals("https://arnafoundation.com/api/app/admin/admin-loans.php", adminLoansUrl)
  }
}

