﻿using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Caching.Memory;
using MovieAppApi.Data;
using MovieAppApi.DTO;
using MovieAppApi.Models;
using MovieAppApi.Service;
using NuGet.Common;

namespace MovieAppApi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class UserController : ControllerBase
    {
        private MovieContext _movieContext;
        private BuildJSON buildJSON;
        private readonly IMemoryCache _cache;

        public UserController(MovieContext movieContext, IMemoryCache memoryCache)
        {
            _movieContext = movieContext;
            this.buildJSON = new BuildJSON();
            _cache = memoryCache;
        }

        [HttpPost("login")]
        public async Task<IActionResult> CheckLogin([FromBody] LoginDTO loginDTO)
        {
            try
            {
                var user = await _movieContext.Users.FirstOrDefaultAsync(u => u.Email == loginDTO.Email);
                if (user != null && loginDTO.Password.Equals(user.Password))
                {
                    return Ok(buildJSON.UserCheckLogin(user));
                }
                return BadRequest("Not match");
            }catch (Exception)
            {
                return BadRequest();
            }   
        }

        [HttpPost("change-password")]
        public async Task<IActionResult> ChangePassword([FromBody] ChangePassDTO changePassDTO)
        {
            try
            {
                var user = await _movieContext.Users.FirstOrDefaultAsync(u => u.Email == changePassDTO.Email);
                if (user == null)
                {
                    return NotFound("This account is'nt exist!");
                }
                else if (changePassDTO.NewPassword != changePassDTO.PasswordConfirm)
                {
                    return BadRequest("Password confirm is not the same password");
                }
                else if (changePassDTO.OldPassword != user.Password)
                {
                    return BadRequest("Old password not match!");
                }
                else
                {
                    user.Password = changePassDTO.NewPassword;
                    await _movieContext.SaveChangesAsync();
                    return Ok();
                }
            }
            catch (Exception)
            {
                return BadRequest();
            }
        }

        [HttpPost("forgot-password-request")]
        public async Task<IActionResult> ForgotPasswordRequest([FromBody] ForgotDTO forgotDTO)
        {
            try
            {
                var existUser = await _movieContext.Users.Where(u => u.Email == forgotDTO.Email).FirstOrDefaultAsync() != null;
                if (existUser)
                {
                    var code = _cache.Get<Code>(forgotDTO.Email);
                    if(code != null)
                    {
                        if(DateTimeOffset.UtcNow.AddMinutes(4) < code.DeadTime)
                        {
                            return BadRequest("Too many request, please wait 1 minute since the last successful request");
                        }
                    }
                    int randomCode = new Random().Next(100100, 999999);
                    _cache.Set(forgotDTO.Email, new Code(randomCode, DateTimeOffset.UtcNow.AddMinutes(5)), TimeSpan.FromMinutes(5));
                    await new PasswordResetService().SendPasswordResetEmail(forgotDTO.Email, randomCode);
                }
                var responseData = new{existUser = existUser};
                return Ok(responseData);
            }
            catch(Exception)
            {
                return BadRequest();
            }
        }

        [HttpPost("forgot-password-checkcode")]
        public IActionResult ForgotPasswordCheckCode([FromBody] ForgotDTO forgotDTO)
        {
            try
            {
                var code = _cache.Get<Code>(forgotDTO.Email);
                var codeMatch = code != null && code.CodeNumber.Equals(forgotDTO.Code);
                var responseData = new
                {
                    codeMatch = codeMatch
                };
                return Ok(responseData);
            }
            catch
            {
                return BadRequest();
            }
        }


        [HttpPost("forgot-password-change")]
        public async Task<IActionResult> ForgotPasswordChange([FromBody] ForgotDTO forgotDTO)
        {
            try
            {
                if(forgotDTO.Password != forgotDTO.PasswordConfirm)
                {
                    return BadRequest("Password confirm is not the same password");
                }
                var user = await _movieContext.Users.Where(u => u.Email == forgotDTO.Email).FirstOrDefaultAsync();
                var code = _cache.Get<Code>(forgotDTO.Email);
                var changeSuccess = false;
                if (user != null && code != null && code.CodeNumber.Equals(forgotDTO.Code))
                {
                    user.Password = forgotDTO.Password;
                    changeSuccess = await _movieContext.SaveChangesAsync() == 1;
                }
                var responseData = new
                {
                    changeSuccess = changeSuccess
                };
                return Ok(responseData);
            }
            catch
            {
                return BadRequest();
            }
        }

        [HttpPost("register")]
        public async Task<IActionResult> Register([FromBody] UserDTO userDTO)
        {
            try
            {
                var user = await _movieContext.Users.FirstOrDefaultAsync(u => u.Email == userDTO.Email);
                if (user != null)
                {
                    return BadRequest("This email is already in use by another account!");
                }
                else if (userDTO.Password != userDTO.PasswordConfirm)
                {
                    return BadRequest("Password confirm is not the same password");
                }
                else
                {
                    User newUser = new User();
                    newUser.Email = userDTO.Email;
                    newUser.Name = userDTO.Name;
                    newUser.Password = userDTO.Password;
                    await _movieContext.Users.AddAsync(newUser);
                    await _movieContext.SaveChangesAsync();
                    return Ok();
                }
            }
            catch (Exception)
            {
                return BadRequest();
            }
        }
    }
}
